import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;
import javax.swing.*;

public class GameMenu extends JPanel implements ActionListener {
    private JFrame frame;
    private JButton playButton, loadMemoryButton, settingsButton, helpButton, exitButton;
    private JLabel title, subtitle;
    private Image cursorImage;
    private Cursor customCursor;
    private Timer timer;
    private int introStep = 0;
    private float introAlpha = 0f; // for fading effect
    private String[] introTexts = { "Dewan Mukto presents...", "a Muxday production", "In The Wildest Dimensions" };
    private Clip hoverSound, clickSound;
    private Font customTitleFont, customMenuFont, customTextFont;
    private boolean showMenu = false;
    private int buttonXPosition = -300;  // Start buttons off-screen
    private Square[] squares; // Array of squares for background animation
    private int squareSpeed = 2; // Speed of square movement

    public GameMenu() {
        // Load custom fonts
        try {
            customTitleFont = Font.createFont(Font.TRUETYPE_FONT, new File("eternal.ttf")).deriveFont(50f);
            customMenuFont = Font.createFont(Font.TRUETYPE_FONT, new File("LeningradDisco.ttf")).deriveFont(24f);
            customTextFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gugi.ttf")).deriveFont(20f);
            cursorImage = Toolkit.getDefaultToolkit().getImage("cursor.png");
            customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load Sounds
        hoverSound = loadSound("hover.wav");
        clickSound = loadSound("click.wav");

        // Setup Frame
        frame = new JFrame("In The Wildest Dimensions");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setCursor(customCursor);

        // Set Layout
        setLayout(null);
        setBackground(Color.BLACK);

        // Title with glitch effects (initially in the center for the intro)
        title = new JLabel("In The Wildest Dimensions");
        title.setFont(customTitleFont);
        title.setForeground(Color.WHITE);
        title.setBounds(340, 300, 1000, 80); // Centered initially
        title.setVisible(false);  // Hide title during intro
        add(title);
        
        // Subtitle for version information
        subtitle = new JLabel("version 0.1 2024Oct15 early alpha");
        subtitle.setFont(customTextFont);
        subtitle.setForeground(Color.WHITE);
        subtitle.setBounds(50, 80, 500, 30); // Position it below the title
        subtitle.setVisible(false);  // Hide initially
        add(subtitle);

        // Create Menu Buttons (initially hidden)
        playButton = createButton("Play", buttonXPosition, 360);
        loadMemoryButton = createButton("Load Memory", buttonXPosition, 420);
        settingsButton = createButton("Settings", buttonXPosition, 480);
        helpButton = createButton("Help", buttonXPosition, 540);
        exitButton = createButton("Exit", buttonXPosition, 600);

        add(playButton);
        add(loadMemoryButton);
        add(settingsButton);
        add(helpButton);
        add(exitButton);

        // Initialize squares for background animation
        int numSquares = 20; // Number of squares
        squares = new Square[numSquares];
        for (int i = 0; i < numSquares; i++) {
            squares[i] = new Square(getWidth() + i * 64, (int)(Math.random() * getHeight())); // Random vertical position
        }

        // Timer for animations
        timer = new Timer(30, this);
        timer.start();

        frame.add(this);
        frame.setVisible(true);
    }

    // Create a Button with hover effects
    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(customMenuFont);
        button.setBounds(x, y, 250, 40);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Add hover and click effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.YELLOW);
                playSound(hoverSound);
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }

            public void mousePressed(MouseEvent e) {
                playSound(clickSound);
            }
        });

        return button;
    }

    // Play Sound Clip
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);  // Rewind
            clip.start();
        }
    }

    // Load Sound
    private Clip loadSound(String filename) {
        try {
            File soundFile = new File(filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error loading sound: " + filename);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable: " + filename);
            e.printStackTrace();
        }
        return null;
    }

    // Paint Background, Intro Text, and Menu Transition
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Handle the intro sequence
        if (introStep < introTexts.length) {
            Graphics2D g2d = (Graphics2D) g;

            // Check if the current intro text is "In The Wildest Dimensions"
            if (introTexts[introStep].equals("In The Wildest Dimensions")) {
                g2d.setFont(customTitleFont);  // Set to customTitleFont
            } else {
                g2d.setFont(customMenuFont);  // Set to default menu font
            }

            // Clamp alpha value between 0 and 1 to avoid errors
            float clampedAlpha = Math.max(0f, Math.min(1f, introAlpha));
            g2d.setColor(new Color(1f, 1f, 1f, clampedAlpha));  // Use clampedAlpha

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(introTexts[introStep]);
            int textX = (getWidth() - textWidth) / 2;
            int textY = getHeight() / 2;

            g2d.drawString(introTexts[introStep], textX, textY);
        }

        // Fade in/out transition
        if (introAlpha < 1f && introStep < introTexts.length) {
            introAlpha += 0.01f;  // Slower fading speed
        } else if (introAlpha >= 1f) {
            introAlpha = 0f;
            introStep++;
        }

        // After intro, show the menu and animate buttons sliding in
        if (introStep >= introTexts.length && !showMenu) {
            title.setVisible(true);  // Display the title
            showMenu = true;
        }

        // Animate title moving from center to top-left corner
        if (showMenu && title.getY() > 20) {
            title.setBounds(50, Math.max(20, title.getY() - 2), 1000, 80);  // Slower movement
        }
        
        // Show subtitle when title is at the top-left
        if (showMenu && title.getY() <= 20) {
            subtitle.setVisible(true); // Show the subtitle
        }

        // Slide in buttons (slower)
        if (showMenu && buttonXPosition < 50) {
            buttonXPosition += 2;  // Slower button slide-in speed
            playButton.setLocation(buttonXPosition, playButton.getY());
            loadMemoryButton.setLocation(buttonXPosition, loadMemoryButton.getY());
            settingsButton.setLocation(buttonXPosition, settingsButton.getY());
            helpButton.setLocation(buttonXPosition, helpButton.getY());
            exitButton.setLocation(buttonXPosition, exitButton.getY());
        }

        // Draw background animation only if the menu is shown
        if (showMenu) {
            drawBackground(g);
        }
    }

    // Draw background animation
    private void drawBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw squares in the background
        for (Square square : squares) {
            g2d.setColor(square.color);
            square.x -= squareSpeed; // Move square to the left

            // Check if the square has moved off the left side of the screen
            if (square.x < -square.size) {
                square.x = getWidth(); // Reset position to the right side of the screen
                square.y = (int)(Math.random() * getHeight()); // Randomize vertical position
            }

            // Draw the square
            g2d.fillRect(square.x, square.y, square.size, square.size); // Center the square
        }
    }

    // Main Action Listener for timer events
    public void actionPerformed(ActionEvent e) {
        repaint(); // Repaint the panel
    }

    // Square class representing the animated squares
    class Square {
        int x, y;
        int size = 64; // Square size
        Color color;

        Square(int x, int y) {
            this.x = x;
            this.y = y;
            this.color = new Color((float)Math.random(), 0f, 0f); // Random color
        }
    }

    public static void main(String[] args) {
        new GameMenu(); // Launch the game menu
    }
}
