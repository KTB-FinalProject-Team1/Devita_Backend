package com.devita.domain.post.repository;

import com.devita.domain.post.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 모든 게시물을 페이징하여 조회
    Page<Post> findAll(Pageable pageable);

    // 사용자가 작성한 게시물 조회
    // N+1 고려
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.writer w WHERE w.id = :writerId",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.writer.id = :writerId")
    Page<Post> findByWriterIdWithFetchJoin(@Param("writerId") Long writerId, Pageable pageable);

    // 비관적 락을 사용한 게시물 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithPessimisticLock(@Param("postId") Long postId);

    // 좋아요 증가 (Redis 구현에서 사용)
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.likes = p.likes + :likesToAdd WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId, @Param("likesToAdd") Long likesToAdd);
}