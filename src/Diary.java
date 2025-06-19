package main;

import java.time.LocalDate;

public class Diary {
    private final LocalDate date;
    private final String emotion;
    private final String content;

    public Diary(LocalDate date, String emotion, String content) {
        this.date = date;
        this.emotion = emotion;
        this.content = content;
    }

    public LocalDate getDate() { return date; }
    public String getEmotion() { return emotion; }
    public String getContent() { return content; }

    // Diary -> txt 파일 저장 시 사용 / Diary String 으로 저장
    public String toFileString() {
        return "[날짜] " + date + "\n"
                + "[감정] " + emotion + "\n"
                + "[일기] " + content + "\n";
    }

    // txt 파일 일기 (String) -> Diary 사용 / 일기 Diary 객체로 불러오기
    public static Diary fromFileString(String fileContent) {
        // 줄바꿈 기준으로 String 배열 생성
        String[] lines = fileContent.split("\n");
        // 날짜
        LocalDate date = LocalDate.parse(lines[0].substring(5).trim());
        // 감정
        String emotion = lines[1].substring(5).trim();

        // 일기 내용
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 2; i < lines.length; i++) {
            // 세 번째 줄의 "[일기] "만 제거
            contentBuilder.append(lines[i].substring(i == 2 ? 5 : 0)).append("\n");
        }
        String content = contentBuilder.toString().trim();

        return new Diary(date, emotion, content);
    }
}
