//package com.devita;
//import com.devita.domain.category.domain.Category;
//import com.devita.domain.category.repository.CategoryRepository;
//import com.devita.domain.todo.dto.TodoReqDTO;
//import com.devita.domain.todo.service.TodoService;
//import com.devita.domain.user.service.AuthService;
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class DataInitializer {
//
//    private final AuthService authService;
//    private final CategoryRepository categoryRepository;
//    private final TodoService todoService;
//
//
//    @PostConstruct
//    @Transactional
//    public void init() {
//        log.info("초기 유저 데이터를 생성합니다.");
//
//        // 기본 유저 데이터를 생성하기 위한 속성 구성
//        Map<String, Object> profile = new HashMap<>();
//        profile.put("nickname", "DefaultUser");
//        profile.put("profile_image_url", "https://example.com/default_profile.png");
//
//        Map<String, Object> kakaoAccount = new HashMap<>();
//        kakaoAccount.put("email", "default_user@example.com");
//        kakaoAccount.put("profile", profile);
//
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("kakao_account", kakaoAccount);
//
//        // 기본 유저 생성
//        try {
//            authService.loadUser(attributes);
//        } catch (Exception e) {
//            log.error("초기 유저 데이터 생성 실패: {}", e.getMessage());
//        }
//
//        log.info("초기 유저 데이터 생성 완료.");
//
//        try {
//            // 자율 미션 카테고리의 ID 조회
//            Category category = categoryRepository.findByUserIdAndName(1L, "자율 미션")
//                    .orElseThrow(() -> new IllegalStateException("카테고리 '자율 미션'이 존재하지 않습니다."));
//
//            // 자율 미션 카테고리에 할 일 추가
//            TodoReqDTO todoReqDTO = new TodoReqDTO(
//                    category.getId(),
//                    "기본 자율 미션",
//                    LocalDate.now() // 오늘 날짜로 설정
//            );
//
//            // 사용자 ID로 기본 유저의 ID를 설정 (예: 1L)
//            todoService.addTodo(1L, todoReqDTO);
//
//            log.info("기본 자율 미션 추가 완료.");
//
//        } catch (Exception e) {
//            log.error("초기 자율 미션 데이터 생성 실패: {}", e.getMessage());
//        }
//    }
//}