package shop.genieus.user.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.DateTimeException;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BirthInfo {
  private static final int RESIDENT_ID_FRONT_LENGTH = 7;
  private static final int DATE_PART_LENGTH = 6;

  @Column(name = "birthdate", nullable = false)
  private LocalDate birthdate;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 10, nullable = false)
  private Gender gender;

  private BirthInfo(String datePart, char identifierDigit) {
    validate(datePart, identifierDigit);
    this.birthdate = parseToDate(datePart, identifierDigit);
    this.gender = KoreanResidentIdPolicy.parseGender(identifierDigit);
  }

  public static BirthInfo fromResidentIdFront(String frontPart) {
    if (frontPart == null || frontPart.length() != RESIDENT_ID_FRONT_LENGTH) {
      throw new IllegalArgumentException(
          "주민등록번호 앞 " + RESIDENT_ID_FRONT_LENGTH + "자리는 YYMMDD+성별식별코드 형식이어야 합니다");
    }

    String datePart = frontPart.substring(0, DATE_PART_LENGTH);
    char identifierDigit = frontPart.charAt(DATE_PART_LENGTH);

    return new BirthInfo(datePart, identifierDigit);
  }

  private void validate(String datePart, char identifierDigit) {
    if (!datePart.matches("\\d{" + DATE_PART_LENGTH + "}")) {
      throw new IllegalArgumentException("생년월일은 " + DATE_PART_LENGTH + "자리 숫자여야 합니다");
    }

    if (!KoreanResidentIdPolicy.isValidIdentifierDigit(identifierDigit)) {
      throw new IllegalArgumentException("주민등록번호 식별자 자리는 1~4 중 하나여야 합니다");
    }

    try {
      LocalDate birth = parseToDate(datePart, identifierDigit);

      if (birth.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("생년월일은 현재 날짜보다 이전이어야 합니다");
      }

      if (birth.isAfter(LocalDate.now().minusYears(14))) {
        throw new IllegalArgumentException("만 14세 이상만 등록할 수 있습니다");
      }

    } catch (DateTimeException e) {
      throw new IllegalArgumentException("유효하지 않은 날짜입니다: " + e.getMessage());
    }
  }

  private LocalDate parseToDate(String datePart, char identifierDigit) {
    boolean is1900s = KoreanResidentIdPolicy.isBornIn1900s(identifierDigit);
    int year = Integer.parseInt(datePart.substring(0, 2));
    int fullYear = is1900s ? 1900 + year : 2000 + year;
    int month = Integer.parseInt(datePart.substring(2, 4));
    int day = Integer.parseInt(datePart.substring(4, 6));
    return LocalDate.of(fullYear, month, day);
  }

  private static class KoreanResidentIdPolicy {

    public static Gender parseGender(char identifierDigit) {
      return switch (identifierDigit) {
        case '1', '3' -> Gender.MALE;
        case '2', '4' -> Gender.FEMALE;
        default -> throw new IllegalArgumentException("알 수 없는 주민등록번호 식별자입니다: " + identifierDigit);
      };
    }

    public static boolean isBornIn1900s(char identifierDigit) {
      return identifierDigit == '1' || identifierDigit == '2';
    }

    public static char toIdentifierDigit(Gender gender, boolean is1900s) {
      return switch (gender) {
        case MALE -> is1900s ? '1' : '3';
        case FEMALE -> is1900s ? '2' : '4';
      };
    }

    public static boolean isValidIdentifierDigit(char digit) {
      return digit == '1' || digit == '2' || digit == '3' || digit == '4';
    }
  }
}
