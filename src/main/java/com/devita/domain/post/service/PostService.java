package com.devita.domain.post.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.follow.service.FollowService;
import com.devita.domain.post.domain.Post;
import com.devita.domain.post.dto.*;
import com.devita.domain.post.repository.PostRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.devita.common.exception.ErrorCode.ACCESS_DENIED;
import static com.devita.common.exception.ErrorCode.POST_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String LIKE_KEY_PREFIX = "post:like:";
    private static final String LIKE_COUNT_KEY_PREFIX = "post:like_count:";

    // 게시물 생성
    public Post addPost(Long userId, PostReqDTO postReqDTO) {
        User writer = getWriter(userId);

        Post post = Post.builder()
                .writer(writer)
                .title(postReqDTO.title())
                .description(postReqDTO.description())
                .build();

        return postRepository.save(post);
    }

    // 게시물 삭제
    public void deletePost(Long userId, Long postId) {
        Post post = validateWriter(userId, postId);

        postRepository.delete(post);
    }

    // 게시물 수정
    public PostResDTO updatePost(Long userId, Long postId, PostReqDTO postReqDTO) {
        Post post = validateWriter(userId, postId);

        post.updatePost(postReqDTO.title(), postReqDTO.description());
        postRepository.save(post);

        return new PostResDTO(postId, post.getWriter().getNickname(), post.getTitle(), post.getDescription(), getLikeCount(postId), post.getViews());
    }

    // 게시물 페이징 조회
    public List<PostsResDTO> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // 페이지 번호와 페이지 크기를 기반으로 Pageable 객체 생성
        Page<Post> postPage = postRepository.findAll(pageable);

        return postPage.getContent().stream()
                .map(post -> new PostsResDTO(post.getId(), post.getTitle(), post.getDescription(), getLikeCount(post.getId()), post.getViews()))
                .toList();
    }

    // 게시물 상세 조회
    public PostResDTO getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));

        if (!post.getWriter().getId().equals(userId)) {
            post.increaseView();
            postRepository.save(post);
        }

        return new PostResDTO(postId, post.getWriter().getNickname(), post.getTitle(), post.getDescription(), getLikeCount(postId), post.getViews());
    }

    // 작성한 게시물 조회
    public List<PostsResDTO> getMyPosts(Long userId, int page, int size) {
        getWriter(userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Post> postPage = postRepository.findByWriterIdWithFetchJoin(userId, pageable);

        return postPage.getContent().stream()
                .map(post -> new PostsResDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getDescription(),
                        getLikeCount(post.getId()),
                        post.getViews()
                ))
                .toList();
    }

    private User getWriter(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    // 작성자 유무 확인
    private Post validateWriter(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getWriter().getId().equals(userId))
                .orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED));
        return post;
    }

    @Transactional
    public Long likePost(Long userId, Long postId) {
        String likeKey = LIKE_KEY_PREFIX + postId;
        String countKey = LIKE_COUNT_KEY_PREFIX + postId;
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        // 이미 좋아요를 눌렀는지 확인하고 추가
        boolean isAdded = setOps.add(likeKey, userId.toString()) == 1;
        if (isAdded) {
            // 좋아요 카운트 증가
            valueOps.increment(countKey);
        }
        return Long.parseLong(valueOps.get(countKey));
    }

    @Transactional
    public Long unlikePost(Long userId, Long postId) {
        String likeKey = LIKE_KEY_PREFIX + postId;
        String countKey = LIKE_COUNT_KEY_PREFIX + postId;
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        // 좋아요 제거
        boolean isRemoved = setOps.remove(likeKey, userId.toString()) == 1;
        if (isRemoved) {
            // 좋아요 카운트 감소
            valueOps.decrement(countKey);
        }
        return Long.parseLong(valueOps.get(countKey));
    }

    public boolean isLikedByUser(Long userId, Long postId) {
        String key = LIKE_KEY_PREFIX + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }

    public Long getLikeCount(Long postId) {
        String countKey = LIKE_COUNT_KEY_PREFIX + postId;
        String count = redisTemplate.opsForValue().get(countKey);
        return count != null ? Long.parseLong(count) : 0L;
    }

    public Page<FollowingPostResponseDTO> getFollowingUsersPosts(Long userId, Pageable pageable) {
        return postRepository.findFollowingUsersPosts(userId, pageable)
                .map(post -> FollowingPostResponseDTO.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .description(post.getDescription())
                        .writerId(post.getWriter().getId())
                        .writerNickname(post.getWriter().getNickname())
                        .likes(getLikeCount(post.getId()))  // Redis에서 좋아요 수 조회
                        .views(post.getViews())
                        .createdAt(post.getCreatedAt())
                        .isLiked(isLikedByUser(userId, post.getId()))  // 현재 사용자의 좋아요 여부 확인
                        .build());
    }

    public Page<AllPostsResDTO> getAllPosts(Long userId, Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> AllPostsResDTO.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .description(post.getDescription())
                        .writerId(post.getWriter().getId())
                        .writerNickname(post.getWriter().getNickname())
                        .likes(getLikeCount(post.getId()))
                        .views(post.getViews())
                        .createdAt(post.getCreatedAt())
                        .isLiked(isLikedByUser(userId, post.getId()))  // 현재 사용자의 좋아요 여부 확인
                        .build());
    }

    // LikeTest - Redis
    @Transactional
    public Long likePostWithRedis(Long userId, Long postId) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String countKey = LIKE_COUNT_KEY_PREFIX + postId;
        valueOps.increment(countKey);
        return Long.parseLong(valueOps.get(countKey));
    }

    // LikeTest - Pessimistic-Lock)
    @Transactional
    public Long increaseLikePessimistic(Long userId, Long postId) {
        Post post = postRepository.findByIdWithPessimisticLock(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        post.updateLikes(post.getLikes() + 1);
        postRepository.save(post);
        return post.getLikes();
    }

    // LikeTest - Optimistic-Lock)
    @Transactional
    public Long increaseLikeOptimistic(Long userId, Long postId) {
        int maxRetry = 100; // 최대 시도 횟수
        int attempt = 0;    // 현재 시도 횟수
        while (attempt < maxRetry) {
            try {
                // 트랜잭션 내에서 좋아요 증가 처리
                return updateLikeCount(postId);
            } catch (RuntimeException e) {
                attempt++;
                if (attempt >= maxRetry) {
                    throw new AccessDeniedException(ACCESS_DENIED);
                }
            }
        }
        // 실패할 경우 기본값 반환 (일반적으로 여기 도달하지 않음)
        throw new IllegalStateException("Unexpected state in like update process.");
    }

    // LikeTest - Default
    @Transactional
    public Long updateLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        post.updateLikes(post.getLikes() + 1); // 좋아요 증가
        return post.getLikes();
    }
}