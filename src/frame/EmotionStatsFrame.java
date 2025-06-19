package main.frame;

import main.Diary;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class EmotionStatsFrame extends JFrame {

    // UI 구성 요소
    private final JLabel titleLabel = new JLabel("감정 통계", SwingConstants.CENTER);   // 상단 제목
    private final JLabel adviceLabel = new JLabel(" ", SwingConstants.CENTER);         // 감정 조언
    private final EmotionGraphPanel graphPanel = new EmotionGraphPanel();              // 그래프 패널

    public EmotionStatsFrame() {
        setTitle("감정 통계");
        setSize(600, 450);
        setLayout(new BorderLayout());

        // 콤보박스용 월 리스트
        String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};

        // 현재 월을 가져와서 monthBox 에서 선택
        String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
        JComboBox<String> monthBox = new JComboBox<>(months);
        monthBox.setSelectedItem(currentMonth);

        JButton btn = new JButton("보기");

        // 라벨 폰트 설정
        titleLabel.setFont(new Font("Apple SD Gothic Neo", Font.BOLD, 24));
        adviceLabel.setFont(new Font("Apple SD Gothic Neo", Font.PLAIN, 16));

        // 상단 제목 라벨 추가
        add(titleLabel, BorderLayout.NORTH);
        // 그래프 중앙에 배치
        add(graphPanel, BorderLayout.CENTER);

        // controlPanel (월 선택 콤보박스 + 버튼 배치)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(new JLabel("월 선택:"));
        controlPanel.add(monthBox);
        controlPanel.add(btn);

        // 하단: 조언 + 컨트롤 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(adviceLabel, BorderLayout.CENTER);      // 조언
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);      // 월 선택 버튼
        add(bottomPanel, BorderLayout.SOUTH);

        graphPanel.setPreferredSize(new Dimension(580, 180));

        // 버튼 클릭 시 감정 통계 불러오기
        btn.addActionListener(e -> loadEmotionStats((String) monthBox.getSelectedItem()));

        // 실행 시 현재 월 통계 자동 표시
        btn.doClick();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    //  선택한 월의 감정 통계를 불러와서 UI에 반영
    private void loadEmotionStats(String month) {
        Path dir = Paths.get("diaries", month);

        // 폴더가 존재하지 않으면 안내 후 종료
        if (!Files.exists(dir)) {
            updateUI("감정 통계", "해당 월에 일기가 없습니다.", Collections.emptyMap());
            return;
        }

        // Map 생성 <감정, 횟수>
        Map<String, Integer> map = new HashMap<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".txt")).forEach(p -> {
                try {
                    // 일기 파일 내용 → Diary 객체 → 감정 추출
                    Diary d = Diary.fromFileString(Files.readString(p));
                    map.put(d.getEmotion(), map.getOrDefault(d.getEmotion(), 0) + 1);
                } catch (Exception ignored) {}
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 감정 데이터가 없는 경우
        if (map.isEmpty()) {
            updateUI("감정 통계", "감정 데이터가 없습니다.", Collections.emptyMap());
        } else {
            // 최다 감정을 구하고, UI 업데이트
            String top = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
            updateUI(month + "월 감정 통계", getAdvice(top), map);
        }
    }

    // UI 요소(제목, 조언, 그래프) 갱신
    private void updateUI(String title, String advice, Map<String, Integer> data) {
        titleLabel.setText(title);
        adviceLabel.setText(advice);
        graphPanel.setEmotionData(data);
        graphPanel.repaint();
    }

    // 가장 높은 감정에 따른 간단한 조언 반환
    private String getAdvice(String emotion) {
        return switch (emotion) {
            case "기쁨" -> "좋은 감정이네요! 이 행복을 오래 유지하세요.";
            case "슬픔" -> "슬플 땐 가까운 사람과 대화해 보세요.";
            case "화남" -> "화날 땐 잠시 쉬며 심호흡을 해보세요.";
            case "불안" -> "불안할 땐 명상이나 산책을 해보세요.";
            case "지침" -> "피곤할 땐 마음 편하게 숙면을 취하는 것도 좋아요.";
            case "평온" -> "평온함을 유지하며 하루를 즐기세요.";
            default -> "감정을 잘 살펴보세요.";
        };
    }

    // 감정 막대그래프 패널
    static class EmotionGraphPanel extends JPanel {
        private Map<String, Integer> emotionData = Collections.emptyMap();

        // 감정별 색상 매핑
        private static final Map<String, Color> COLOR_MAP = Map.of(
                "기쁨", Color.ORANGE,
                "슬픔", Color.BLUE,
                "화남", Color.RED,
                "불안", Color.GRAY,
                "지침", Color.PINK,
                "평온", Color.GREEN
        );

        public void setEmotionData(Map<String, Integer> data) {
            this.emotionData = data;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 데이터가 없으면 메시지 출력
            if (emotionData.isEmpty()) {
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Apple SD Gothic Neo", Font.PLAIN, 16));
                g.drawString("표시할 감정 데이터가 없습니다.", 20, 30);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            // 안티앨리어싱으로 부드럽게
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int height = getHeight();
            int padding = 40;
            int barWidth = 40;
            int spacing = 30;
            int max = Collections.max(emotionData.values());
            int x = padding;

            // 감정 데이터를 내림차순 정렬
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(emotionData.entrySet());
            sortedList.sort((a, b) -> b.getValue() - a.getValue());

            // 감정 막대 그리기
            for (var entry : sortedList) {
                String emotion = entry.getKey();
                int value = entry.getValue();
                int barHeight = (int) ((double) value / max * (height - 2 * padding));

                g2.setColor(COLOR_MAP.getOrDefault(emotion, Color.GRAY));
                g2.fillRect(x, height - padding - barHeight, barWidth, barHeight);

                // 감정명, 횟수 텍스트 출력
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Apple SD Gothic Neo", Font.PLAIN, 14));
                g2.drawString(emotion, x, height - padding + 15);
                g2.drawString(value + "회", x, height - padding - barHeight - 5);

                x += barWidth + spacing;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(580, 180);
        }
    }
}
