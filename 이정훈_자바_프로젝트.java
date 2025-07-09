package 이정훈_자바_프로젝트;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class 이정훈_자바_프로젝트 extends JFrame {
    private String[] strAnimal = new String[10];
    private String[] strFruit = new String[10];
    private Color[] animalColors = new Color[10];
    private Color[] fruitColors = new Color[10];
    private int[][] arrayGame;
    private int[][] checkGame;
    private JButton[][] buttons;
    private int firstSelectX = -1;
    private int firstSelectY = -1;
    private int secondSelectX = -1;
    private int secondSelectY = -1;
    private int failCount = 0;
    private boolean isAnimalGame = true;
    private Timer gameTimer;
    private int elapsedTime = 0;
    private final int GAME_DURATION = 5 *20 * 1000; // 5 minutes

    private void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                elapsedTime += 1000;
                int remainingTime = GAME_DURATION - elapsedTime;
                if (remainingTime <= 0) {
                    gameTimer.stop();
                    String message = String.format("시간이 초과되었습니다.\n지금까지 총 %d 번 실수하셨습니다", failCount);
                    JOptionPane.showMessageDialog(이정훈_자바_프로젝트.this, message, "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else {
                    int minutes = remainingTime / (60 * 1000);
                    int seconds = (remainingTime % (60 * 1000)) / 1000;
                    setTitle(String.format("게임 진행 중 (%02d:%02d)", minutes, seconds));
                }
            }
        });
        gameTimer.start();
    }

    public 이정훈_자바_프로젝트() {
        initAnimalName();
        initFruitName();
        initColors();
        initGameArray();
        shuffleGame();

        setTitle("게임 선택");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JButton animalButton = new JButton("동물 맞추기 게임");
        JButton fruitButton = new JButton("과일 맞추기 게임");

        animalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isAnimalGame = true;
                initializeGame();
                startTimer();
            }
        });

        fruitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isAnimalGame = false;
                initializeGame();
                startTimer();
            }
        });

        add(animalButton);
        add(fruitButton);

        setSize(300, 100);
        setVisible(true);
    }

    public void initAnimalName() {
        strAnimal[0] = "원숭이";
        strAnimal[1] = "하마";
        strAnimal[2] = "강아지";
        strAnimal[3] = "고양이";
        strAnimal[4] = "돼지";
        strAnimal[5] = "코끼리";
        strAnimal[6] = "기린";
        strAnimal[7] = "낙타";
        strAnimal[8] = "타조";
        strAnimal[9] = "호랑이";
    }

    public void initFruitName() {
        strFruit[0] = "사과";
        strFruit[1] = "바나나";
        strFruit[2] = "딸기";
        strFruit[3] = "수박";
        strFruit[4] = "포도";
        strFruit[5] = "오렌지";
        strFruit[6] = "레몬";
        strFruit[7] = "망고";
        strFruit[8] = "체리";
        strFruit[9] = "파인애플";
    }

    public void initColors() {
        animalColors[0] = Color.RED;
        animalColors[1] = Color.GREEN;
        animalColors[2] = Color.BLUE;
        animalColors[3] = Color.ORANGE;
        animalColors[4] = Color.CYAN;
        animalColors[5] = Color.MAGENTA;
        animalColors[6] = Color.YELLOW;
        animalColors[7] = Color.PINK;
        animalColors[8] = Color.GRAY;
        animalColors[9] = Color.LIGHT_GRAY;

        fruitColors[0] = new Color(255, 204, 204); // Light Red
        fruitColors[1] = new Color(204, 255, 204); // Light Green
        fruitColors[2] = new Color(204, 204, 255); // Light Blue
        fruitColors[3] = new Color(255, 255, 204); // Light Yellow
        fruitColors[4] = new Color(204, 255, 255); // Light Cyan
        fruitColors[5] = new Color(255, 204, 255); // Light Magenta
        fruitColors[6] = new Color(255, 255, 153); // Light Yellow-Green
        fruitColors[7] = new Color(255, 204, 153); // Light Orange
        fruitColors[8] = new Color(204, 204, 255); // Light Lavender
        fruitColors[9] = new Color(255, 204, 229); // Light Pink
    }

    public void initGameArray() {
        arrayGame = new int[4][5];
        checkGame = new int[4][5];
        buttons = new JButton[4][5];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                arrayGame[i][j] = -1;
            }
        }
    }

    public void shuffleGame() {
        int numItems = isAnimalGame ? strAnimal.length : strFruit.length;

        for (int i = 0; i < numItems; i++) {
            for (int j = 0; j < 2; j++) {
                int pos = getEmptyPosition();
                int x = convPosToX(pos);
                int y = convPosToY(pos);
                arrayGame[x][y] = i;
            }
        }
    }

    public int getEmptyPosition() {
        while (true) {
            int randPos = (int) (Math.random() * 20); // 0~19
            int x = convPosToX(randPos);
            int y = convPosToY(randPos);
            if (arrayGame[x][y] == -1) {
                return randPos;
            }
        }
    }

    public int convPosToX(int pos) {
        return pos / 5;
    }

    public int convPosToY(int pos) {
        return pos % 5;
    }

    public void showItem(int x, int y) {
        String item = isAnimalGame ? strAnimal[arrayGame[x][y]] : strFruit[arrayGame[x][y]];
        buttons[x][y].setText(item);
        buttons[x][y].setBackground(isAnimalGame ? animalColors[arrayGame[x][y]] : fruitColors[arrayGame[x][y]]);
    }

    public void hideItem(int x, int y) {
        buttons[x][y].setText("");
        buttons[x][y].setBackground(Color.BLUE);
    }

    public boolean foundAllItems() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (checkGame[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkSelectedCards() {
        if (checkGame[firstSelectX][firstSelectY] == 0
                && checkGame[secondSelectX][secondSelectY] == 0
                && arrayGame[firstSelectX][firstSelectY] == arrayGame[secondSelectX][secondSelectY]) {
            System.out.printf("\n\n빙고! : %s 발견\n\n", isAnimalGame ? strAnimal[arrayGame[firstSelectX][firstSelectY]] : strFruit[arrayGame[firstSelectX][firstSelectY]]);
            checkGame[firstSelectX][firstSelectY] = 1;
            checkGame[secondSelectX][secondSelectY] = 1;
            buttons[firstSelectX][firstSelectY].setEnabled(false);
            buttons[secondSelectX][secondSelectY].setEnabled(false);
        } else {
            System.out.println("\n\n땡 !! (틀렸거나, 이미 뒤집힌 카드입니다)");
            System.out.printf("%d, %d : %s\n", firstSelectX, firstSelectY, isAnimalGame ? strAnimal[arrayGame[firstSelectX][firstSelectY]] : strFruit[arrayGame[firstSelectX][firstSelectY]]);
            System.out.printf("%d, %d : %s\n", secondSelectX, secondSelectY, isAnimalGame ? strAnimal[arrayGame[secondSelectX][secondSelectY]] : strFruit[arrayGame[secondSelectX][secondSelectY]]);
            System.out.println("\n\n");
            hideItem(firstSelectX, firstSelectY);
            hideItem(secondSelectX, secondSelectY);
            failCount++;
        }

        firstSelectX = -1;
        firstSelectY = -1;
        secondSelectX = -1;
        secondSelectY = -1;

        if (foundAllItems()) {
            String message = String.format("축하합니다! 모든 %s을(를) 다 찾았네요\n지금까지 총 %d 번 실수하셨습니다", isAnimalGame ? "동물" : "과일", failCount);
            JOptionPane.showMessageDialog(this, message, "게임 종료", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public void initializeGame() {
        getContentPane().removeAll();
        setLayout(new GridLayout(4, 5));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.BLUE);
                buttons[i][j].setPreferredSize(new Dimension(100, 100));
                buttons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        JButton clickedButton = (JButton) event.getSource();

                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 5; y++) {
                                if (clickedButton == buttons[x][y]) {
                                    if (firstSelectX == -1 && firstSelectY == -1) {
                                        firstSelectX = x;
                                        firstSelectY = y;
                                        showItem(x, y);
                                    } else if (firstSelectX != -1 && firstSelectY != -1 && secondSelectX == -1 && secondSelectY == -1) {
                                        secondSelectX = x;
                                        secondSelectY = y;
                                        showItem(x, y);
                                        checkSelectedCards();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                });
                add(buttons[i][j]);
            }
        }

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        new 이정훈_자바_프로젝트();
    }
}
