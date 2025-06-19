package main.frame;

import main.Diary;
import main.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DiaryReadFrame extends JFrame {
    private final JTextArea diaryArea;
    private final JButton saveButton, deleteButton;
    private final JSpinner dateSpinner;
    private LocalDate selectedDate;

    // 일기 불러오기 화면
    public DiaryReadFrame() {
        // 창 설정
        setTitle("일기 조회");
        setSize(500, 500);
        setLayout(new BorderLayout());

        // 날짜 선택 패널
        JPanel topPanel = new JPanel(new FlowLayout());
        // 날짜 모델 (현재 날짜 기준, 최소, 최대, 하루씩 변경)
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);

        // 날짜 선택 스피너 생성
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        JButton loadButton = new JButton("불러오기");

        topPanel.add(new JLabel("날짜 선택:"));
        topPanel.add(dateSpinner);
        topPanel.add(loadButton);
        add(topPanel, BorderLayout.NORTH);

        String[] emotions = {"기쁨", "슬픔", "화남", "불안", "지침", "평온"};
        JComboBox<String> emotionBox = new JComboBox<>(emotions);
        emotionBox.setEnabled(false);  // 기본은 비활성화 상태로 시작

        // 중앙 패널: 감정 박스 + 일기 내용
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(emotionBox, BorderLayout.NORTH);

        // 일기 내용 영역
        diaryArea = new JTextArea();
        diaryArea.setLineWrap(true);
        diaryArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(diaryArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 하단 버튼
        JPanel bottomPanel = new JPanel();
        saveButton = new JButton("수정");
        deleteButton = new JButton("삭제");

        // 버튼 비활성화
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        bottomPanel.add(saveButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 불러오기 버튼 클릭 시
        loadButton.addActionListener(e -> {
            Date selected = (Date) dateSpinner.getValue();
            selectedDate = selected.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            try {
                Diary diary = FileManager.loadDiary(selectedDate);
                // 기존 일기 텍스트
                diaryArea.setText(diary.getContent());

                // 기존 감정 및 감정창 활성화
                emotionBox.setSelectedItem(diary.getEmotion());
                emotionBox.setEnabled(true);

                // 버튼 활성화
                saveButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } catch (IOException ex) {
                diaryArea.setText("");

                emotionBox.setSelectedIndex(-1);
                emotionBox.setEnabled(false);

                JOptionPane.showMessageDialog(this, "해당 날짜의 일기가 없습니다.");
                saveButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });

        // 수정 버튼 클릭시
        saveButton.addActionListener(e -> {
            try {
                // 수정 사항으로 다이어리 객체 생성 후 저장
                Diary updated = new Diary(selectedDate, (String)emotionBox.getSelectedItem(), diaryArea.getText());
                FileManager.saveDiary(updated);
                JOptionPane.showMessageDialog(this, "일기가 수정되었습니다.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "저장 중 오류: " + ex.getMessage());
            }
        });

        // 삭제 버튼 클릭시
        deleteButton.addActionListener(e -> {
            // 경고문 표시
            int result = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // 선택 날짜를 기반으로 일기 삭제
                    FileManager.deleteDiary(selectedDate);
                    diaryArea.setText("");
                    JOptionPane.showMessageDialog(this, "일기가 삭제되었습니다.");

                    // 버튼 비활성화
                    saveButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "삭제 중 오류 발생: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}