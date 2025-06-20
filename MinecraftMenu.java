import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MinecraftMenu extends JFrame {
    private static final int WIDTH = 854;
    private static final int HEIGHT = 480;
    private static final String TITLE = "Minecraft";

    private List<MenuButton> buttons;
    private JLabel backgroundLabel;
    private Font minecraftFont;

    public MinecraftMenu() {
        initComponents();
        setupUI();
        setupButtons();
        setupEventListeners();
    }

    private void initComponents() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            // 加载Minecraft字体
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT,
                            getClass().getResourceAsStream("/assets/fonts/minecraft.ttf"))
                    .deriveFont(Font.PLAIN, 24);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(minecraftFont);
        } catch (Exception e) {
            // 如果字体加载失败，使用默认字体
            minecraftFont = new Font("Arial", Font.PLAIN, 24);
        }

        buttons = new ArrayList<>();
    }

    private void setupUI() {
        // 创建背景面板
        backgroundLabel = new JLabel();
        backgroundLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        backgroundLabel.setLayout(null);

        // 设置背景图
        try {
            ImageIcon backgroundImage = new ImageIcon(
                    getClass().getResource("/assets/textures/gui/title/background/panorama_0.png"));
            backgroundLabel.setIcon(backgroundImage);
        } catch (Exception e) {
            // 如果背景图加载失败，设置为黑色背景
            backgroundLabel.setBackground(Color.BLACK);
            backgroundLabel.setOpaque(true);
        }

        add(backgroundLabel);
    }

    private void setupButtons() {
        // 创建按钮
        MenuButton singlePlayerButton = new MenuButton("单人游戏", 282, 200);
        MenuButton multiPlayerButton = new MenuButton("多人游戏", 282, 250);
        MenuButton optionsButton = new MenuButton("选项", 282, 300);
        MenuButton quitButton = new MenuButton("退出游戏", 282, 350);

        // 添加按钮到列表
        buttons.add(singlePlayerButton);
        buttons.add(multiPlayerButton);
        buttons.add(optionsButton);
        buttons.add(quitButton);

        // 将按钮添加到背景面板
        for (MenuButton button : buttons) {
            backgroundLabel.add(button);
        }
    }

    private void setupEventListeners() {
        // 为按钮添加事件监听器
        for (MenuButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = button.getText();
                    switch (text) {
                        case "单人游戏":
                            JOptionPane.showMessageDialog(MinecraftMenu.this, "启动单人游戏");
                            break;
                        case "多人游戏":
                            JOptionPane.showMessageDialog(MinecraftMenu.this, "启动多人游戏");
                            break;
                        case "选项":
                            JOptionPane.showMessageDialog(MinecraftMenu.this, "打开选项菜单");
                            break;
                        case "退出游戏":
                            System.exit(0);
                            break;
                    }
                }
            });
        }
    }

    // 自定义按钮类
    private class MenuButton extends JButton {
        private static final int WIDTH = 300;
        private static final int HEIGHT = 40;

        public MenuButton(String text, int x, int y) {
            super(text);
            setBounds(x, y, WIDTH, HEIGHT);
            setFont(minecraftFont);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);

            // 设置按钮背景
            setContentAreaFilled(false);

            // 添加鼠标事件监听器
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.YELLOW);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(Color.WHITE);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            // 启用抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制按钮背景
            if (getModel().isPressed()) {
                g2d.setColor(new Color(64, 64, 64));
            } else if (getModel().isRollover()) {
                g2d.setColor(new Color(128, 128, 128));
            } else {
                g2d.setColor(new Color(96, 96, 96));
            }
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // 绘制按钮边框
            g2d.setColor(Color.WHITE);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // 绘制按钮文字
            FontMetrics fm = g2d.getFontMetrics(getFont());
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2d.setFont(getFont());
            g2d.drawString(getText(), textX, textY);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MinecraftMenu().setVisible(true);
            }
        });
    }
}    public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}