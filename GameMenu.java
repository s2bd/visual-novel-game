import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;
import javax.swing.*;

public class GameMenu extends JPanel implements ActionListener {
    private JFrame frame;
    private JButton playButton, loadMemoryButton, settingsButton, helpButton, exitButton, backButton;
    private JLabel title, subtitle;
    private JTextArea helpTextArea;
    private Image cursorImage, adam;
    private Cursor customCursor;
    private Timer timer;
    private int introStep = 0;
    private float introAlpha = 0f; // for fading effect
    private String[] introTexts = { "Dewan Mukto presents...", "a Muxday production", "In The Wildest Dimensions" };
    private Clip hoverSound, clickSound;
    private Font customTitleFont, customMenuFont, customTextFont;
    private boolean showMenu = false;
    private boolean showHelp = false;
    private boolean reverseMenu = false; // Track if we are reversing the menu
    private int buttonXPosition = -300;  // Start buttons off-screen
    private int helpTextXPosition = -400;
    private int adamImageYPosition;
    private Square[] squares; // Array of squares for background animation
    private int squareSpeed = 2; // Speed of square movement
    private boolean helpActive = false;
    private boolean showAdamImage = false;
    private int titleTargetY = 600;  // Bottom-left position for title
    private int subtitleTargetY = 660;  // Bottom-left position for subtitle
    
    public GameMenu() {
        // Load custom fonts and cursor once
        loadResources();

        // Setup Frame
        frame = new JFrame("In The Wildest Dimensions");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setCursor(customCursor);

        // Set Layout
        setLayout(null);
        setBackground(Color.BLACK);

        // Initialize title and subtitle labels
        initializeTitleAndSubtitle();

        // Create Menu Buttons (initially hidden)
        initializeButtons();

        // Initialize squares for background animation
        initializeSquares();
        
        adamImageYPosition = getHeight(); // Start below the screen

        // Timer for animations
        timer = new Timer(30, this);
        timer.start();

        frame.add(this);
        frame.setVisible(true);
    }
    
    private void loadResources() {
        // Load custom fonts
        try {
            customTitleFont = Font.createFont(Font.TRUETYPE_FONT, new File("eternal.ttf")).deriveFont(50f);
            customMenuFont = Font.createFont(Font.TRUETYPE_FONT, new File("LeningradDisco.ttf")).deriveFont(24f);
            customTextFont = Font.createFont(Font.TRUETYPE_FONT, new File("Gugi.ttf")).deriveFont(20f);
            cursorImage = Toolkit.getDefaultToolkit().getImage("cursor.png");
            customCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor");
            adam = Toolkit.getDefaultToolkit().getImage("adam.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load Sounds once
        hoverSound = loadSound("hover.wav");
        clickSound = loadSound("click.wav");
    }

    private void initializeTitleAndSubtitle() {
        title = new JLabel("In The Wildest Dimensions");
        title.setFont(customTitleFont);
        title.setForeground(Color.WHITE);
        title.setBounds(340, 300, 1000, 80); // Centered initially
        title.setVisible(false);  // Hide title during intro
        add(title);
        
        subtitle = new JLabel("version 0.1 2024Oct15 early alpha");
        subtitle.setFont(customTextFont);
        subtitle.setForeground(Color.WHITE);
        subtitle.setBounds(50, 80, 500, 30); // Position it below the title
        subtitle.setVisible(false);  // Hide initially
        add(subtitle);
    }

    private void initializeButtons() {
        playButton = createButton("Play", buttonXPosition, 360);
        loadMemoryButton = createButton("Load Memory", buttonXPosition, 420);
        settingsButton = createButton("Settings", buttonXPosition, 480);
        helpButton = createButton("Help", buttonXPosition, 540);
        exitButton = createButton("Exit", buttonXPosition, 600);
        
        helpButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                playSound(clickSound);
                reverseMenu = true;  // Trigger reverse animation
                loadHelpContent();  // Load the help content when help button is pressed
                showHelp = true;  // Set to show the help text
            }
        });
        
        helpTextArea = new JTextArea();
        helpTextArea.setFont(customTextFont);
        helpTextArea.setBackground(new Color(0, 0, 0, 0));
        helpTextArea.setForeground(Color.WHITE);
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setBounds(-400, 200, 700, 450); // Set initial position off-screen
        helpTextArea.setVisible(false);  // Initially hidden
        add(helpTextArea);
        
        backButton = createButton("Back", buttonXPosition, 640); // Position it below the exit button
        backButton.setVisible(false); // Initially hidden
        backButton.addActionListener(e -> {
            showHelp = false;  // Hide help content
            helpTextArea.setVisible(false); // Hide the help text area
            backButton.setVisible(false); // Hide the back button
            reverseMenu = false;    // Reset reverse menu flag
            buttonXPosition = -300;  // Reset button position off-screen
            updateButtonPositions(buttonXPosition); // Update button positions to reset
            helpTextXPosition = -400; // Reset help text position
        });
        add(backButton);


        exitButton.addActionListener(e -> System.exit(0));  // Exit the program

        add(playButton);
        add(loadMemoryButton);
        add(settingsButton);
        add(helpButton);
        add(exitButton);
    }

    private void initializeSquares() {
        int numSquares = 20; // Number of squares
        squares = new Square[numSquares];
        for (int i = 0; i < numSquares; i++) {
            squares[i] = new Square(getWidth() + i * 64, (int)(Math.random() * getHeight()));
        }
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(customMenuFont);
        button.setBounds(x, y, 250, 40);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

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

    private Clip loadSound(String filename) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    private void loadHelpContent() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("help.txt"));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            helpTextArea.setText(content.toString());
            helpTextArea.setVisible(true);  // Make it visible after loading content
            backButton.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    
        // Handle the intro sequence
        if (introStep < introTexts.length) {
            drawIntroText(g2d);
        } else if (showMenu) {
            drawMenuAnimation();
        }
    
        // Draw background animation
        if (showMenu) {
            drawBackground(g2d);
        }
        
        if (showAdamImage) {
            int scaledHeight = getHeight();
            int scaledWidth = (int)((float)adam.getWidth(null) / adam.getHeight(null) * scaledHeight);
            Image scaledImage = adam.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            g.drawImage(adam, getWidth() - adam.getWidth(null), adamImageYPosition, null);
        }
    
        // Reverse the menu when help is triggered
        if (reverseMenu && buttonXPosition > -300) {
            buttonXPosition -= 5;  // Move buttons off-screen
            updateButtonPositions(buttonXPosition);
        }
        
        // Update help text position
        if (showHelp && helpTextXPosition < 50) {
            helpTextXPosition += 5;  // Move text area to the right
            helpTextArea.setLocation(helpTextXPosition, helpTextArea.getY());
            backButton.setLocation(100, 650);
        }
    }
    
    private void drawIntroText(Graphics2D g2d) {
        String introText = introTexts[introStep];
        g2d.setFont(introText.equals("In The Wildest Dimensions") ? customTitleFont : customMenuFont);

        // Clamp alpha value between 0 and 1
        float clampedAlpha = Math.max(0f, Math.min(1f, introAlpha));
        g2d.setColor(new Color(1f, 1f, 1f, clampedAlpha));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(introText);
        int textX = (getWidth() - textWidth) / 2;
        int textY = getHeight() / 2;
        g2d.drawString(introText, textX, textY);

        // Fade in/out effect
        if (introAlpha < 1f) {
            introAlpha += 0.01f;
        } else {
            introAlpha = 0f;
            introStep++;
        }

        // After the last intro step, show the menu
        if (introStep >= introTexts.length) {
            showMenu = true;  // Set showMenu to true after intro finishes
            title.setVisible(true);  // Show title
        }
        
        if (introStep >= introTexts.length && !showAdamImage) {
            showAdamImage = true; // Start showing the image
        }
        
        if (showAdamImage && adamImageYPosition > getHeight() - adam.getHeight(null)) {
            adamImageYPosition -= 5; // Move the image up
        }

    }

    private void drawMenuAnimation() {
        if (title.getY() > 20) {
            title.setLocation(50, Math.max(20, title.getY() - 2));  // Slower movement
        }
        if (title.getY() <= 20) {
            subtitle.setVisible(true);  // Show subtitle
        }

        if (buttonXPosition < 50) {
            buttonXPosition += 2;  // Slide buttons in
            updateButtonPositions(buttonXPosition);
        }
    }

    private void updateButtonPositions(int xPosition) {
        playButton.setLocation(xPosition, playButton.getY());
        loadMemoryButton.setLocation(xPosition, loadMemoryButton.getY());
        settingsButton.setLocation(xPosition, settingsButton.getY());
        helpButton.setLocation(xPosition, helpButton.getY());
        exitButton.setLocation(xPosition, exitButton.getY());
    }

    private void drawBackground(Graphics2D g2d) {
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

    public void actionPerformed(ActionEvent e) {
        // Repaint the panel every frame
        repaint();
    }

    // Class to represent animated squares
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
        new GameMenu();
    }
}
