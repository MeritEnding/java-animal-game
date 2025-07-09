package 이정훈_자바_프로젝트;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class 이정훈_자바_프로젝트 extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameTheme currentTheme;
    private Difficulty currentDifficulty;

    public 이정훈_자바_프로젝트() {
        setTitle("이정훈의 카드 맞추기 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 850);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. 시작 화면
        mainPanel.add(new StartScreen(this::showDifficultyScreen), "Start");
        // 2. 난이도 선택 화면 (테마 선택 후 동적 생성)
        // 3. 게임 화면 (난이도 선택 후 동적 생성)
        // 4. 게임 종료 화면 (게임 종료 후 동적 생성)

        add(mainPanel);
        setVisible(true);
    }

    public void showDifficultyScreen(GameTheme theme) {
        this.currentTheme = theme;
        DifficultyScreen difficultyScreen = new DifficultyScreen(
                e -> startGame(Difficulty.EASY),
                e -> startGame(Difficulty.NORMAL),
                e -> startGame(Difficulty.HARD)
        );
        mainPanel.add(difficultyScreen, "Difficulty");
        cardLayout.show(mainPanel, "Difficulty");
    }

    public void startGame(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        GameScreen gameScreen = new GameScreen(currentTheme, currentDifficulty, this::showEndScreen, this::showStartScreen);
        mainPanel.add(gameScreen, "Game");
        cardLayout.show(mainPanel, "Game");
    }

    public void showEndScreen(String message, boolean isWin) {
        EndScreen endScreen = new EndScreen(message, isWin,
                e -> showDifficultyScreen(currentTheme),
                e -> showStartScreen()
        );
        mainPanel.add(endScreen, "End");
        cardLayout.show(mainPanel, "End");
    }

    public void showStartScreen() {
        cardLayout.show(mainPanel, "Start");
    }

    public static void main(String[] args) {
        // UI를 더 예쁘게 만들기 위해 Nimbus LookAndFeel 적용
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

// =================================================================================
// 화면 구성 요소 (Panels)
// =================================================================================


class StartScreen extends JPanel {
    public StartScreen(ThemeSelectListener listener) {
        setLayout(new BorderLayout(20, 30));
        setBorder(new EmptyBorder(80, 80, 80, 80));
        setBackground(UIFactory.COLOR_BACKGROUND);

        JLabel titleLabel = UIFactory.createLabel("카드 맞추기 게임", UIFactory.FONT_TITLE_MAIN);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setOpaque(false);

        GameTheme animalTheme = new GameTheme("Animal");
        GameTheme fruitTheme = new GameTheme("Fruit");

        JButton animalButton = UIFactory.createButton("동물 친구들 🐵", animalTheme.cardBack, e -> listener.onThemeSelected(animalTheme));
        JButton fruitButton = UIFactory.createButton("새콤달콤 과일 🍓", fruitTheme.cardBack, e -> listener.onThemeSelected(fruitTheme));

        buttonPanel.add(animalButton);
        buttonPanel.add(fruitButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    @FunctionalInterface
    interface ThemeSelectListener {
        void onThemeSelected(GameTheme theme);
    }
}


class DifficultyScreen extends JPanel {
    public DifficultyScreen(ActionListener onEasy, ActionListener onNormal, ActionListener onHard) {
        setLayout(new BorderLayout(20, 30));
        setBorder(new EmptyBorder(80, 80, 80, 80));
        setBackground(UIFactory.COLOR_BACKGROUND);

        JLabel titleLabel = UIFactory.createLabel("난이도를 선택하세요", UIFactory.FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        buttonPanel.setOpaque(false);

        JButton easyButton = UIFactory.createButton("Easy", UIFactory.COLOR_EASY, onEasy);
        JButton normalButton = UIFactory.createButton("Normal", UIFactory.COLOR_NORMAL, onNormal);
        JButton hardButton = UIFactory.createButton("Hard", UIFactory.COLOR_HARD, onHard);

        buttonPanel.add(easyButton);
        buttonPanel.add(normalButton);
        buttonPanel.add(hardButton);

        add(buttonPanel, BorderLayout.CENTER);
    }
}

class EndScreen extends JPanel {
    public EndScreen(String message, boolean isWin, ActionListener onRetry, ActionListener onMenu) {
        setLayout(new BorderLayout(20, 30));
        setBorder(new EmptyBorder(80, 80, 80, 80));
        setBackground(isWin ? new Color(220, 255, 220) : new Color(255, 220, 220));

        JLabel titleLabel = UIFactory.createLabel(message, UIFactory.FONT_TITLE);
        titleLabel.setForeground(isWin ? new Color(0, 100, 0) : new Color(139, 0, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));


        JButton retryButton = UIFactory.createButton("다시하기", UIFactory.COLOR_NORMAL, onRetry);
        JButton menuButton = UIFactory.createButton("메인 메뉴", UIFactory.COLOR_HARD, onMenu);

        buttonPanel.add(retryButton);
        buttonPanel.add(menuButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}


// =================================================================================
// 게임 로직 핵심 (GameScreen)
// =================================================================================

class GameScreen extends JPanel {
    private final GameTheme theme;
    private final Difficulty difficulty;
    private final EndGameListener endGameListener;
    private final Runnable showStartScreenListener;

    private final JButton[][] buttons;
    private int[][] cardValues;
    private boolean[][] matched;

    private int firstSelectX = -1, firstSelectY = -1;
    private boolean isChecking = false;

    private Timer gameTimer;
    private int remainingSeconds;
    private int failCount;

    private JLabel timeLabel;
    private JLabel failLabel;

    public GameScreen(GameTheme theme, Difficulty difficulty, EndGameListener endGameListener, Runnable showStartScreenListener) {
        this.theme = theme;
        this.difficulty = difficulty;
        this.endGameListener = endGameListener;
        this.showStartScreenListener = showStartScreenListener;

        this.buttons = new JButton[difficulty.rows][difficulty.cols];
        this.cardValues = new int[difficulty.rows][difficulty.cols];
        this.matched = new boolean[difficulty.rows][difficulty.cols];
        this.remainingSeconds = difficulty.timeLimit;
        this.failCount = difficulty.failLimit;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(theme.background);

        initializeGameData();
        setupUI();
        startTimer();

        // 모든 난이도에서 미리보기 기능 활성화
        peekCards();
    }

    private void initializeGameData() {
        int numPairs = difficulty.rows * difficulty.cols / 2;
        String[] source = theme.themeName.equals("Animal") ? Data.ANIMALS : Data.FRUITS;

        List<Integer> cards = new ArrayList<>();
        for (int i = 0; i < numPairs; i++) {
            cards.add(i);
            cards.add(i);
        }
        Collections.shuffle(cards);

        int k = 0;
        for (int i = 0; i < difficulty.rows; i++) {
            for (int j = 0; j < difficulty.cols; j++) {
                cardValues[i][j] = cards.get(k++);
            }
        }
    }

    private void setupUI() {
        // 상단 정보 패널
        JPanel infoPanel = new JPanel(new BorderLayout(20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        // 홈 버튼
        JButton homeButton = UIFactory.createButton("🏠", theme.cardBack, e -> {
            if(gameTimer != null) gameTimer.stop();
            showStartScreenListener.run();
        });
        infoPanel.add(homeButton, BorderLayout.WEST);

        // 중앙 정보 (시간, 실패)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        statusPanel.setOpaque(false);

        timeLabel = UIFactory.createLabel("⏳ " + formatTime(remainingSeconds), UIFactory.FONT_INFO);
        statusPanel.add(timeLabel);

        failLabel = UIFactory.createLabel("💣 " + failCount, UIFactory.FONT_INFO);
        statusPanel.add(failLabel);

        infoPanel.add(statusPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);

        // 게임 카드 패널
        JPanel gamePanel = new JPanel(new GridLayout(difficulty.rows, difficulty.cols, 8, 8));
        gamePanel.setOpaque(false);

        String[] source = theme.themeName.equals("Animal") ? Data.ANIMALS : Data.FRUITS;

        for (int i = 0; i < difficulty.rows; i++) {
            for (int j = 0; j < difficulty.cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(UIFactory.FONT_CARD);
                buttons[i][j].setBackground(theme.cardBack);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(theme.background, 2));

                final int x = i;
                final int y = j;

                buttons[i][j].addActionListener(e -> onCardClick(x, y));
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (buttons[x][y].isEnabled() && buttons[x][y].getText().isEmpty()) {
                            buttons[x][y].setBackground(theme.cardHover);
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (buttons[x][y].isEnabled() && buttons[x][y].getText().isEmpty()) {
                            buttons[x][y].setBackground(theme.cardBack);
                        }
                    }
                });
                gamePanel.add(buttons[i][j]);
            }
        }
        add(gamePanel, BorderLayout.CENTER);
    }

    private void onCardClick(int x, int y) {
        if (isChecking || matched[x][y] || (firstSelectX == x && firstSelectY == y)) {
            return;
        }

        SoundManager.play("click.wav");
        showItem(x, y);

        if (firstSelectX == -1) {
            firstSelectX = x;
            firstSelectY = y;
        } else {
            isChecking = true;
            checkSelectedCards(x, y);
        }
    }

    private void checkSelectedCards(int secondX, int secondY) {
        if (cardValues[firstSelectX][firstSelectY] == cardValues[secondX][secondY]) {
            // 정답
            SoundManager.play("match.wav");
            matched[firstSelectX][firstSelectY] = true;
            matched[secondX][secondY] = true;

            animateMatch(buttons[firstSelectX][firstSelectY]);
            animateMatch(buttons[secondX][secondY]);
            buttons[firstSelectX][firstSelectY].setEnabled(false);
            buttons[secondX][secondY].setEnabled(false);

            resetSelection();

            if (foundAllItems()) {
                if (gameTimer != null) gameTimer.stop();
                endGameListener.onGameEnd("🎉 축하합니다! 게임 클리어! 🎉", true);
            }
        } else {
            // 오답
            SoundManager.play("mismatch.wav");
            failCount--;
            failLabel.setText("💣 " + failCount);
            if (failCount <= 0) {
                if (gameTimer != null) gameTimer.stop();
                endGameListener.onGameEnd("실패 횟수 초과!", false);
                return;
            }
            Timer mismatchTimer = new Timer(800, e -> {
                hideItem(firstSelectX, firstSelectY);
                hideItem(secondX, secondY);
                resetSelection();
            });
            mismatchTimer.setRepeats(false);
            mismatchTimer.start();
        }
    }

    private void resetSelection() {
        firstSelectX = -1;
        firstSelectY = -1;
        isChecking = false;
    }

    private void showItem(int x, int y) {
        String[] source = theme.themeName.equals("Animal") ? Data.ANIMALS : Data.FRUITS;
        buttons[x][y].setText(source[cardValues[x][y]]);
        buttons[x][y].setBackground(theme.cardFront);
    }

    private void hideItem(int x, int y) {
        if (!matched[x][y]) {
            buttons[x][y].setText("");
            buttons[x][y].setBackground(theme.cardBack);
        }
    }

    private boolean foundAllItems() {
        for (boolean[] row : matched) {
            for (boolean cell : row) {
                if (!cell) return false;
            }
        }
        return true;
    }

    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            remainingSeconds--;
            timeLabel.setText("⏳ " + formatTime(remainingSeconds));
            if (remainingSeconds <= 0) {
                gameTimer.stop();
                endGameListener.onGameEnd("시간 초과!", false);
            }
        });
        gameTimer.start();
    }

    private void peekCards() {
        // 모든 카드를 잠시 보여주는 기능
        for(int i=0; i<difficulty.rows; i++) {
            for (int j=0; j<difficulty.cols; j++) {
                showItem(i, j);
                buttons[i][j].setEnabled(false);
            }
        }

        Timer peekTimer = new Timer(2000, e -> {
             for(int i=0; i<difficulty.rows; i++) {
                for (int j=0; j<difficulty.cols; j++) {
                    hideItem(i, j);
                    buttons[i][j].setEnabled(true);
                }
            }
        });
        peekTimer.setRepeats(false);
        peekTimer.start();
    }

    private void animateMatch(JButton button) {
        final Color originalColor = theme.cardFront;
        final Color flashColor = Color.YELLOW;
        Timer flashTimer = new Timer(150, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count % 2 == 0) {
                    button.setBackground(flashColor);
                } else {
                    button.setBackground(originalColor);
                }
                count++;
                if (count > 2) {
                    button.setBackground(theme.cardMatched);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        flashTimer.start();
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    @FunctionalInterface
    interface EndGameListener {
        void onGameEnd(String message, boolean isWin);
    }
}

// =================================================================================
// 데이터 및 유틸리티 클래스
// =================================================================================

/** 게임 테마 정의 */
class GameTheme {
    String themeName;
    Color background;
    Color cardBack;
    Color cardHover;
    Color cardMatched;
    Color cardFront;

    GameTheme(String themeName) {
        this.themeName = themeName;
        if (themeName.equals("Animal")) {
            this.background = new Color(222, 238, 214);
            this.cardBack = new Color(120, 153, 93);
            this.cardHover = new Color(140, 173, 113);
            this.cardMatched = new Color(200, 200, 200);
            this.cardFront = new Color(255, 215, 130);
        } else { // Fruit 테마
            this.background = new Color(255, 235, 225);
            this.cardBack = new Color(255, 130, 130);
            this.cardHover = new Color(255, 150, 150);
            this.cardMatched = new Color(200, 200, 200);
            this.cardFront = new Color(170, 220, 255);
        }
    }
}


class Data {
    public static final String[] ANIMALS = {
        "🐵", "🦁", "🐶", "🐱", "🐷", "🐘", "🐻", "🐪", "🐧", "🐯",
        "🐰", "🐸", "🐮", "🐔", "🐴", "🦉", "🦋", "🐢"
    };
    public static final String[] FRUITS = {
        "🍎", "🍌", "🍓", "🍉", "🍇", "🍑", "🍋", "🍐", "🍒", "🍍",
        "🥝", "🌰", "🥑", "🍅", "🍆", "🌽", "🌶️", "🍄" // 🥥를 🥦로 교체
    };
}


enum Difficulty {
    EASY(4, 5, 300, 30),   // 5분, 실패 30회
    NORMAL(5, 6, 420, 30), // 7분, 실패 30회
    HARD(6, 6, 600, 30);   // 10분, 실패 30회

    final int rows, cols, timeLimit, failLimit;

    Difficulty(int rows, int cols, int timeLimit, int failLimit) {
        this.rows = rows;
        this.cols = cols;
        this.timeLimit = timeLimit; // 초 단위
        this.failLimit = failLimit; // 횟수
    }
}


class UIFactory {
    public static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    public static final Color COLOR_EASY = new Color(102, 187, 106);
    public static final Color COLOR_NORMAL = new Color(66, 165, 245);
    public static final Color COLOR_HARD = new Color(239, 83, 80);

    public static final Font FONT_TITLE_MAIN = new Font("나눔고딕", Font.BOLD, 48);
    public static final Font FONT_TITLE = new Font("나눔고딕", Font.BOLD, 36);
    public static final Font FONT_BUTTON = new Font("나눔고딕", Font.BOLD, 24);
    public static final Font FONT_INFO = new Font("나눔고딕", Font.BOLD, 22);
    public static final Font FONT_CARD = new Font("SansSerif", Font.BOLD, 40);

    public static JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text) {
             @Override
             protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 그림자
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                } else {
                    g2.setColor(Color.BLACK.darker());
                    g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 25, 25);
                    // 버튼
                    g2.setColor(getModel().isRollover() ? bgColor.brighter() : bgColor);
                    g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 25, 25);
                }

                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
             }
        };

        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(15,30,15,30));
        button.addActionListener(listener);
        button.setContentAreaFilled(false);
        return button;
    }

    public static JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }
}


class SoundManager {
    public static void play(String fileName) {
        /*
         * 효과음 파일(.wav)을 프로젝트 폴더 내 'resources' 폴더에 넣고 사용하세요.
         * 예시: 프로젝트폴더/resources/click.wav
         *
         * try (Clip clip = AudioSystem.getClip()) {
         * URL url = SoundManager.class.getResource("/resources/" + fileName);
         * if (url != null) {
         * try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(url)) {
         * clip.open(audioIn);
         * clip.start();
         * }
         * }
         * } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
         * // System.err.println("Sound Error: " + e.getMessage());
         * }
        */
    }
}
