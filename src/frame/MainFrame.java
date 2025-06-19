package main.frame;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        // 창 설정
        setTitle("감정 일기 앱");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬

        getContentPane().setBackground(new Color(255, 216, 156)); // 배경색 (주황)

        // 제목
        JLabel titleLabel = new JLabel("감정 일기장", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Apple SD Gothic Neo", Font.BOLD, 40));
        titleLabel.setForeground(new Color(133, 163, 137)); // 제목 텍스트 색 (초록)
        add(titleLabel, BorderLayout.NORTH);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        buttonPanel.setOpaque(false); // 패널 투명 처리

        JButton writeButton = crateStylingButton("Write Diary", DiaryWriteFrame::new);
        JButton readButton = crateStylingButton("Read Diary", DiaryReadFrame::new );
        JButton statsButton = crateStylingButton("Emotion stats", EmotionStatsFrame::new);

        buttonPanel.add(writeButton);
        buttonPanel.add(readButton);
        buttonPanel.add(statsButton);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // 버튼 스타일링
    private JButton crateStylingButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Apple SD Gothic Neo", Font.PLAIN, 30));
        button.setForeground(new Color(133, 163, 137));
        button.setBackground(new Color(240, 200, 140));
        button.setPreferredSize(new Dimension(550, 130));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
}