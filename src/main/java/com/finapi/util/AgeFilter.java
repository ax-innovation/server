package com.finapi.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * join_member 텍스트 + 상품명 키워드로 나이 조건 가입 가능 여부 판단
 *
 * 처리하는 패턴:
 *   상품명에 "아이", "키즈", "어린이" 등 포함 → 미성년자 전용으로 판단
 *   "만 17세 미만"      → maxAge = 16
 *   "15세 이하"         → maxAge = 15
 *   "만14세이상"        → minAge = 14
 *   "만 17세 이상"      → minAge = 17
 *   "만19세~만34세"     → minAge = 19, maxAge = 34
 */
public class AgeFilter {

    // 미성년자 전용 상품명 키워드
    private static final List<String> CHILD_KEYWORDS =
        List.of("아이", "키즈", "어린이", "주니어", "child", "kids", "junior");

    // 미성년자 전용 패턴 (미만/이하)
    private static final Pattern MAX_AGE_PATTERN =
        Pattern.compile("(\\d+)세\\s*(미만|이하)");

    // 최소 나이 패턴 (이상)
    private static final Pattern MIN_AGE_PATTERN =
        Pattern.compile("(\\d+)세\\s*이상");

    // 범위 패턴 (n세~m세)
    private static final Pattern RANGE_PATTERN =
        Pattern.compile("(\\d+)세.*?~.*?(\\d+)세");

    /**
     * 사용자 나이가 가입 조건에 맞는지 확인
     * @param joinMember  join_member 컬럼 텍스트
     * @param productName 상품명 (키워드 기반 필터링용)
     * @param userAge     사용자 나이
     * @return true: 가입 가능 / false: 가입 불가
     */
    public static boolean isEligible(String joinMember, String productName, int userAge) {

        // 1. 상품명 키워드 체크 (아이, 키즈, 어린이 등 → 미성년자 전용)
        if (productName != null) {
            String lowerName = productName.toLowerCase();
            for (String keyword : CHILD_KEYWORDS) {
                if (lowerName.contains(keyword)) return false;
            }
        }

        if (joinMember == null || joinMember.isBlank()) return true;

        // 2. 범위 패턴 체크 (예: 만19세~만34세)
        Matcher rangeMatcher = RANGE_PATTERN.matcher(joinMember);
        if (rangeMatcher.find()) {
            int minAge = Integer.parseInt(rangeMatcher.group(1));
            int maxAge = Integer.parseInt(rangeMatcher.group(2));
            return userAge >= minAge && userAge <= maxAge;
        }

        // 3. 미만/이하 체크
        Matcher maxMatcher = MAX_AGE_PATTERN.matcher(joinMember);
        if (maxMatcher.find()) {
            int limitAge = Integer.parseInt(maxMatcher.group(1));
            String condition = maxMatcher.group(2);
            if ("미만".equals(condition)) return userAge < limitAge;
            if ("이하".equals(condition)) return userAge <= limitAge;
        }

        // 4. 이상 체크
        Matcher minMatcher = MIN_AGE_PATTERN.matcher(joinMember);
        if (minMatcher.find()) {
            int limitAge = Integer.parseInt(minMatcher.group(1));
            return userAge >= limitAge;
        }

        return true; // 나이 조건 없으면 가입 가능
    }
}
