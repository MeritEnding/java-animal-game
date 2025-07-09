package 이정훈_자바_프로젝트;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class 이정훈_자바_프로젝트 extends JFrame {

    // --- 기본 게임 데이터 (호환성 높은 이모지로 최종 수정) ---
    private final String[] strAnimal = {"🐵", "🦁", "🐶", "🐱", "🐷", "🐘", "🐻", "🐪", "🐧", "🐯"};
    private final String[] strFruit = {"🍎", "🍌", "🍓", "🍉", "🍇", "🍑", "🍋", "🍐", "🍒", "🍍"};

    // --- 게임 상태 변수 ---
    private int[][] arrayGame;
    private int[][] checkGame;
    private JButton[][] buttons;
    private int firstSelectX = -1, firstSelectY = -1;
    private int failCount = 0;
    private boolean isChecking = false;

    // --- 테마 및 UI 관련 변수 ---
    private GameTheme currentTheme;
    private JLabel timerLabel;
    private JLabel failCountLabel;

    // --- 타이머 관련 변수 ---
    private Timer gameTimer;
    private int elapsedTime = 0;
    private final int GAME_DURATION = 5 * 60; // 5분 (초 단위)

    /**
     * 테마별 UI 스타일(색상, 폰트)을 정의하는 내부 클래스
     */
    private static class GameTheme {
        String themeName;
        Color background;
        Color cardBack;
        Color cardHover;
        Color cardMatched;
        Color cardText;
        Color cardFront;
        Color accentColor;
        Font titleFont;
        Font buttonFont;
        Font cardFont;
        Font infoFont;

        GameTheme(String themeName) {
            this.themeName = themeName;
            this.titleFont = new Font("나눔고딕", Font.BOLD, 32);
            this.buttonFont = new Font("나눔고딕", Font.BOLD, 18);
            this.cardFont = new Font("SansSerif", Font.BOLD, 50);
            this.infoFont = new Font("나눔고딕", Font.BOLD, 16);

            if (themeName.equals("Animal")) {
                this.background = new Color(250, 240, 230);
                this.cardBack = new Color(188, 143, 143);
                this.cardHover = new Color(205, 155, 155);
                this.cardMatched = new Color(210, 180, 140);
                this.cardText = Color.WHITE;
                this.cardFront = new Color(244, 164, 96);
                this.accentColor = new Color(139, 69, 19);
            } else { // Fruit 테마
                this.background = new Color(240, 255, 240);
                this.cardBack = new Color(60, 179, 113);
                this.cardHover = new Color(70, 200, 130);
                this.cardMatched = new Color(152, 251, 152);
                this.cardText = Color.WHITE;
                this.cardFront = new Color(255, 160, 122);
                this.accentColor = new Color(255, 99, 71);
            }
        }
    }

    /**
     * 메인 생성자: 게임의 초기 화면을 설정합니다.
     */
    public 이정훈_자바_프로젝트() {
        setTitle("카드 맞추기 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        setLocationRelativeTo(null);
        showStartScreen();
        setVisible(true);
    }

    /**
     * 게임 시작 화면을 구성하고 보여줍니다.
     */
    private void showStartScreen() {
        JPanel panel = new JPanel(new BorderLayout(20, 30));
        panel.setBorder(new EmptyBorder(50, 60, 60, 60));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("어떤 게임을 할까요?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 28));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        buttonPanel.setOpaque(false);

        JButton animalButton = createStyledButton("동물 친구들 🐵", new GameTheme("Animal"), () -> startGame(true, new GameTheme("Animal")));
        JButton fruitButton = createStyledButton("새콤달콤 과일 🍓", new GameTheme("Fruit"), () -> startGame(false, new GameTheme("Fruit")));

        buttonPanel.add(animalButton);
        buttonPanel.add(fruitButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * 스타일이 적용된 시작 화면 버튼을 생성합니다.
     */
    private JButton createStyledButton(String text, GameTheme theme, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(theme.buttonFont);
        button.setBackground(theme.cardBack);
        button.setForeground(theme.cardText);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(20, 30, 20, 30));
        button.addActionListener(e -> action.run());

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(theme.cardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(theme.cardBack);
            }
        });
        return button;
    }

    /**
     * 선택된 게임 타입으로 게임을 시작합니다.
     */
    private void startGame(boolean isAnimal, GameTheme theme) {
        this.currentTheme = theme;
        initializeGameData(isAnimal);
        setupGameUI();
        startTimer();
    }

    /**
     * 게임 데이터를 초기화하고 셔플합니다.
     */
    private void initializeGameData(boolean isAnimalGame) {
        arrayGame = new int[4][5];
        checkGame = new int[4][5];
        buttons = new JButton[4][5];
        failCount = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 2; j++) {
                int x, y;
                do {
                    x = (int) (Math.random() * 4);
                    y = (int) (Math.random() * 5);
                } while (arrayGame[x][y] != 0);
                arrayGame[x][y] = (isAnimalGame ? 1 : 101) + i;
            }
        }
    }

    /**
     * 게임 화면 UI를 구성합니다.
     */
    private void setupGameUI() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(10, 10));
        setBackground(currentTheme.background);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        infoPanel.setBackground(currentTheme.background);

        timerLabel = new JLabel("남은 시간: 05:00", SwingConstants.LEFT);
        timerLabel.setFont(currentTheme.infoFont);
        timerLabel.setForeground(currentTheme.accentColor);

        failCountLabel = new JLabel("실패: 0", SwingConstants.RIGHT);
        failCountLabel.setFont(currentTheme.infoFont);
        failCountLabel.setForeground(currentTheme.accentColor);

        infoPanel.add(timerLabel);
        infoPanel.add(failCountLabel);
        add(infoPanel, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel(new GridLayout(4, 5, 10, 10));
        gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        gamePanel.setBackground(currentTheme.background);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(currentTheme.cardFont);
                buttons[i][j].setBackground(currentTheme.cardBack);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(currentTheme.background, 2));
                buttons[i][j].setCursor(new Cursor(Cursor.HAND_CURSOR));

                final int x = i;
                final int y = j;

                buttons[i][j].addActionListener(e -> onCardClick(x, y));
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (buttons[x][y].isEnabled() && buttons[x][y].getText().isEmpty()) {
                            buttons[x][y].setBackground(currentTheme.cardHover);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (buttons[x][y].isEnabled() && buttons[x][y].getText().isEmpty()) {
                            buttons[x][y].setBackground(currentTheme.cardBack);
                        }
                    }
                });
                gamePanel.add(buttons[i][j]);
            }
        }

        add(gamePanel, BorderLayout.CENTER);

        setSize(600, 750);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * 카드 클릭 이벤트를 처리합니다.
     */
    private void onCardClick(int x, int y) {
        if (isChecking || checkGame[x][y] == 1 || (firstSelectX == x && firstSelectY == y)) {
            return;
        }

        showItem(x, y);

        if (firstSelectX == -1) {
            firstSelectX = x;
            firstSelectY = y;
        } else {
            isChecking = true;
            checkSelectedCards(x, y);
        }
    }

    /**
     * 선택된 두 카드를 비교하고 결과를 처리합니다.
     */
    private void checkSelectedCards(int secondX, int secondY) {
        int val1 = arrayGame[firstSelectX][firstSelectY];
        int val2 = arrayGame[secondX][secondY];

        if (val1 == val2) { // 일치
            checkGame[firstSelectX][firstSelectY] = 1;
            checkGame[secondX][secondY] = 1;

            buttons[firstSelectX][firstSelectY].setBackground(currentTheme.cardMatched);
            buttons[secondX][secondY].setBackground(currentTheme.cardMatched);
            buttons[firstSelectX][firstSelectY].setEnabled(false);
            buttons[secondX][secondY].setEnabled(false);

            resetSelection();

            if (foundAllItems()) {
                gameTimer.stop();
                String message = String.format("🎉 축하합니다! 🎉\n모든 카드를 맞추셨습니다!\n총 실패 횟수: %d", failCount);
                showEndGameDialog(message, "게임 클리어!");
            }
        } else { // 불일치
            failCount++;
            updateFailCount();

            Timer mismatchTimer = new Timer(800, e -> {
                hideItem(firstSelectX, firstSelectY);
                hideItem(secondX, secondY);
                resetSelection();
            });
            mismatchTimer.setRepeats(false);
            mismatchTimer.start();
        }
    }

    /**
     * 선택 정보를 초기화합니다.
     */
    private void resetSelection() {
        firstSelectX = -1;
        firstSelectY = -1;
        isChecking = false;
    }

    /**
     * 카드의 내용을 보여줍니다. (카드 앞면)
     */
    private void showItem(int x, int y) {
        int value = arrayGame[x][y];
        String itemEmoji;

        if (value > 100) { // 과일
            itemEmoji = strFruit[value - 101];
        } else { // 동물
            itemEmoji = strAnimal[value - 1];
        }

        buttons[x][y].setText(itemEmoji);
        buttons[x][y].setBackground(currentTheme.cardFront);
    }

    /**
     * 카드를 뒷면으로 뒤집습니다.
     */
    private void hideItem(int x, int y) {
        if (checkGame[x][y] == 0) {
            buttons[x][y].setText("");
            buttons[x][y].setBackground(currentTheme.cardBack);
        }
    }

    /**
     * 모든 카드를 찾았는지 확인합니다.
     */
    private boolean foundAllItems() {
        for (int[] row : checkGame) {
            for (int cell : row) {
                if (cell == 0) return false;
            }
        }
        return true;
    }

    /**
     * 게임 타이머를 시작하고 UI를 업데이트합니다.
     */
    private void startTimer() {
        elapsedTime = 0;
        gameTimer = new Timer(1000, e -> {
            elapsedTime++;
            int remainingTime = GAME_DURATION - elapsedTime;
            if (remainingTime <= 0) {
                gameTimer.stop();
                String message = String.format("시간이 초과되었습니다.\n총 실패 횟수: %d", failCount);
                showEndGameDialog(message, "시간 초과");
            } else {
                updateTimerDisplay(remainingTime);
            }
        });
        gameTimer.start();
    }

    /**
     * 타이머 표시를 업데이트합니다.
     */
    private void updateTimerDisplay(int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("남은 시간: %02d:%02d", minutes, seconds));
    }

    /**
     * 실패 횟수 표시를 업데이트합니다.
     */
    private void updateFailCount() {
        failCountLabel.setText(String.format("실패: %d", failCount));
    }

    /**
     * 게임 종료 시 대화 상자를 표시합니다.
     */
    private void showEndGameDialog(String message, String title) {
        Object[] options = {"다시 시작", "종료"};
        int choice = JOptionPane.showOptionDialog(this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            showStartScreen();
        } else {
            System.exit(0);
        }
    }

    /**
     * 프로그램의 진입점(entry point)입니다.
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nimbus를 사용할 수 없으면 기본 LookAndFeel 사용
        }
        SwingUtilities.invokeLater(이정훈_자바_프로젝트::new);
    }
}
