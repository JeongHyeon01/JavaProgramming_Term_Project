package main;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public class FileManager {
    // 일기 저장
    public static void saveDiary(Diary diary) throws IOException {
        String month = String.format("%02d", diary.getDate().getMonthValue()); // 월(Month) 추출
        Path dir = Paths.get("diaries", month); // 월(Month) 디렉토리 경로 생성
        Files.createDirectories(dir); // 월(Month) 디렉토리 생성

        Path file = dir.resolve(diary.getDate() + ".txt"); // 파일 이름 생성
        Files.writeString(file, diary.toFileString());  // 일기 입력
    }

    // 일기 불러오기
    public static Diary loadDiary(LocalDate date) throws IOException {
        String month = String.format("%02d", date.getMonthValue()); // 월(Month) 추출
        Path file = Paths.get("diaries", month, date + ".txt"); // 파일 경로 생성
        if (!Files.exists(file)) throw new FileNotFoundException("일기 파일이 존재하지 않습니다.");
        return Diary.fromFileString(Files.readString(file)); // 일기(String) -> Diary 반환
    }

    // 일기 삭제
    public static void deleteDiary(LocalDate date) throws IOException {
        String month = String.format("%02d", date.getMonthValue()); // 월(Month) 추출
        Path file = Paths.get("diaries", month, date + ".txt"); // 파일 경로 생성
        if (Files.exists(file)) Files.delete(file); // 파일 제거
    }
}