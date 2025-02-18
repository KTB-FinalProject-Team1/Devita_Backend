package com.devita.domain.follow.service;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.IllegalArgumentException;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.follow.domain.Follow;
import com.devita.domain.follow.dto.FollowCountDTO;
import com.devita.domain.follow.dto.FollowResponseDTO;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import com.devita.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String FOLLOWING_KEY = "user:{userId}:following";
    private static final String FOLLOWER_KEY = "user:{userId}:follower";

    @Transactional
    public void follow(Long userId, Long targetUserId) {

        if (userId.equals(targetUserId)) {
            throw new ResourceNotFoundException(ErrorCode.CANNOT_FOLLOW_YOURSELF);
        }

        // 유저가 존재하는지 확인 (DB에서 조회)
        validateUsersExistence(userId, targetUserId);

        // 팔로잉 및 팔로워 목록 캐시 조회 및 저장
        Set<String> followingIds = getAndCacheFollowingIds(userId);
        Set<String> followerIds = getAndCacheFollowerIds(targetUserId);

        // 팔로우 관계를 Redis에 추가
        addFollowToRedis(userId, targetUserId);

//        DB
//        User follower = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
//
//        User following = userRepository.findById(targetUserId)
//                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
//        if (isFollowing(userId, targetUserId)) {
//            throw new ResourceNotFoundException(ErrorCode.ALREADY_FOLLOWING);
//        }
//
//        Follow follow = Follow.builder()
//                .follower(follower)
//                .following(following)
//                .build();
//
//        followRepository.save(follow);
    }

    // 유저가 존재하는지 확인 (DB에서 조회)
    private void validateUsersExistence(Long userId, Long targetUserId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Set<String> getAndCacheFollowingIds(Long userId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> followingIds = setOps.members(FOLLOWING_KEY.replace("{userId}", userId.toString()));

        // 팔로잉 캐시에 데이터가 없으면 DB에서 팔로잉 목록을 조회
        if (followingIds == null || followingIds.isEmpty()) {
            List<Follow> followings = followRepository.findByFollowerId(userId);
            followingIds = followings.stream()
                    .map(follow -> follow.getFollowing().getId().toString())
                    .collect(Collectors.toSet());

            // 캐시에 DB에서 조회한 팔로잉 목록을 저장
            if (!followingIds.isEmpty()) {
                setOps.add(FOLLOWING_KEY.replace("{userId}", userId.toString()), followingIds.toArray(new String[0]));
                redisTemplate.expire(FOLLOWING_KEY.replace("{userId}", userId.toString()), 25, TimeUnit.HOURS);
            }
        }
        return followingIds;
    }

    public Set<String> getAndCacheFollowerIds(Long targetUserId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> followerIds = setOps.members(FOLLOWER_KEY.replace("{userId}", targetUserId.toString()));

        // 팔로워 캐시에 데이터가 없으면 DB에서 팔로워 목록을 조회
        if (followerIds == null || followerIds.isEmpty()) {
            List<Follow> followers = followRepository.findByFollowingId(targetUserId);
            followerIds = followers.stream()
                    .map(follow -> follow.getFollower().getId().toString())
                    .collect(Collectors.toSet());

            // 캐시에 DB에서 조회한 팔로워 목록을 저장
            if (!followerIds.isEmpty()) {
                setOps.add(FOLLOWER_KEY.replace("{userId}", targetUserId.toString()), followerIds.toArray(new String[0]));
                redisTemplate.expire(FOLLOWER_KEY.replace("{userId}", targetUserId.toString()), 25, TimeUnit.HOURS);
            }
        }
        return followerIds;
    }

    private void addFollowToRedis(Long userId, Long targetUserId) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(FOLLOWING_KEY.replace("{userId}", userId.toString()), String.valueOf(targetUserId));
        setOps.add(FOLLOWER_KEY.replace("{userId}", targetUserId.toString()), String.valueOf(userId));
    }

    @Transactional
    public void unfollow(Long userId, Long targetUserId) {

        // 유저가 존재하는지 확인
        validateUsersExistence(userId, targetUserId);

        // 팔로잉 및 팔로워 목록 캐시 조회 및 저장
        Set<String> followingIds = getAndCacheFollowingIds(userId);
        Set<String> followerIds = getAndCacheFollowerIds(targetUserId);

        // Redis에서 팔로잉 목록에서 제거
        String followingKey = FOLLOWING_KEY.replace("{userId}", userId.toString());
        String followerKey = FOLLOWER_KEY.replace("{userId}", targetUserId.toString());

        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.remove(followingKey, String.valueOf(targetUserId));  // 팔로잉 목록에서 제거
        setOps.remove(followerKey, String.valueOf(userId));  // 팔로워 목록에서 제거

//        DB
//        Follow follow = followRepository.findByFollowerIdAndFollowingId(userId, targetUserId)
//                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.FOLLOW_NOT_FOUND));
//        followRepository.delete(follow);
    }

    public List<FollowResponseDTO> getFollowings(Long userId) {
//        DB
//        List<Follow> followings = followRepository.findByFollowerId(userId);
//        log.info("follow 목록 조회 전");
//        return followings.stream()
//                .map(follow -> FollowResponseDTO.from(follow, true))
//                .toList();

        Set<String> followingIds = getAndCacheFollowingIds(userId);

        // 팔로잉 목록을 Redis에서 가져왔으면 DTO로 변환하여 반환
        return followingIds.stream()
                .map(followingId -> getUser(Long.valueOf(followingId)))  // true는 팔로잉 여부
                .map(FollowResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<FollowResponseDTO> getFollowers(Long userId) {
//      DB
//      List<Follow> followers = followRepository.findByFollowingId(userId);
//        return followers.stream()
//                .map(follow -> FollowResponseDTO.from(follow, false))
//                .toList();

        // Redis에서 팔로잉 목록을 가져오고 캐시가 없다면 DB에서 조회
        Set<String> followerIds = getAndCacheFollowerIds(userId);

        // 팔로잉 목록을 Redis에서 가져왔으면 DTO로 변환하여 반환
        return followerIds.stream()
                .map(followerId -> getUser(Long.valueOf(followerId)))  // true는 팔로잉 여부
                .map(FollowResponseDTO::from)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean isFollowing(Long userId, Long targetUserId) {
//        return followRepository.existsByFollowerIdAndFollowingId(userId, targetUserId);
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return Boolean.TRUE.equals(setOps.isMember(FOLLOWING_KEY.replace("{userId}", userId.toString()), String.valueOf(targetUserId)));
    }

    public FollowCountDTO getFollowCount(Long userId) {
//        long followingCount = followRepository.countByFollowerId(userId);
//        long followerCount = followRepository.countByFollowingId(userId);
//        return new FollowCountDTO(followingCount, followerCount);

        // Redis에서 팔로잉 수를 가져옴
        Long followingCount = redisTemplate.opsForSet().size(FOLLOWING_KEY);
        // Redis에서 팔로워 수를 가져옴
        Long followerCount = redisTemplate.opsForSet().size(FOLLOWER_KEY);

        // 팔로잉 수가 Redis에서 0이거나 null인 경우 DB에서 가져옴
        if (followingCount == null || followingCount == 0) {
            followingCount = followRepository.countByFollowerId(userId);
        }

        // 팔로워 수가 Redis에서 0이거나 null인 경우 DB에서 가져옴
        if (followerCount == null || followerCount == 0) {
            followerCount = followRepository.countByFollowingId(userId);
        }

        return new FollowCountDTO(followingCount != null ? followingCount : 0, followerCount != null ? followerCount : 0);
    }
}