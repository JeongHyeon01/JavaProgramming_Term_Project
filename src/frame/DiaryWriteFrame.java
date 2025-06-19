package main.frame;

import main.Diary;
import main.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class DiaryWriteFrame extends JFrame {
    public DiaryWriteFrame() {
        // 창 설정
        setTitle("일기 작성");
        setSize(500, 500);
        setLayout(new BorderLayout());

        // 오늘 날짜
        LocalDate today = LocalDate.now();
        JLabel dateLabel = new JLabel(today.toString(), SwingConstants.CENTER);
        dateLabel.setFont(new Font("Apple SD Gothic Neo", Font.BOLD, 16));
        add(dateLabel, BorderLayout.NORTH);

        // 중앙 패널 생성
        JPanel centerPanel = new JPanel(new BorderLayout());
        // 감정 선택
        String[] emotions = {"기쁨", "슬픔", "화남", "불안", "지침", "평온"};
        JComboBox<String> emotionBox = new JComboBox<>(emotions);
        centerPanel.add(emotionBox, BorderLayout.NORTH);

        // 일기 입력
        JTextArea diaryArea = new JTextArea();
        diaryArea.setLineWrap(true);
        diaryArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(diaryArea), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 저장 버튼 생성
        JButton saveButton = getJButton(emotionBox, diaryArea, today);
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // 버튼 생성
    private JButton getJButton(JComboBox<String> emotionBox, JTextArea diaryArea, LocalDate today) {
        JButton saveButton = new JButton("저장");

        // 버튼 클릭시 일기 저장
        saveButton.addActionListener(e -> {
            String emotion = (String) emotionBox.getSelectedItem();
            String content = diaryArea.getText();
            Diary diary = new Diary(today, emotion, content);
            try {
                FileManager.saveDiary(diary);
                JOptionPane.showMessageDialog(this, "일기가 저장되었습니다!");
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "저장 중 오류 발생: " + ex.getMessage());
            }
        });
        return saveButton;
    }
}
