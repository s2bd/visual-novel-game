// DEPRECATED VERSION OF THE MENU
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;  // For playing sounds
import java.io.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class GameMenu extends JPanel implements ActionListener {
private JFrame frame;
private JButton playButton, loadMemoryButton, settingsButton, helpButton, exitButton;
private JLabel title;
private Timer timer;
private int introStep = 0;
private float introAlpha = 0f; // for fading effect
private String[] introTexts = { "Dewan Mukto presents...", "a Muxday production", "In The Wildest Dimensions" };
private Clip hoverSound, clickSound;
private Font customTitleFont, customMenuFont;
private boolean showMenu = false;
private int buttonXPosition = -300;  // Start buttons off-screen

public GameMenu() {
// Load custom fonts
try {
customTitleFont = Font.createFont(Font.TRUETYPE_FONT, new File("eternal.ttf")).deriveFont(50f);
customMenuFont = Font.createFont(Font.TRUETYPE_FONT, new File("LeningradDisco.ttf")).deriveFont(24f);
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
// Use the correct path to your WAV files
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
introAlpha += 0.02f;  // Slower fading speed
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

// Slide in buttons (slower)
if (showMenu && buttonXPosition < 50) {
buttonXPosition += 2;  // Slower button slide-in speed
playButton.setLocation(buttonXPosition, playButton.getY());
loadMemoryButton.setLocation(buttonXPosition, loadMemoryButton.getY());
settingsButton.setLocation(buttonXPosition, settingsButton.getY());
helpButton.setLocation(buttonXPosition, helpButton.getY());
exitButton.setLocation(buttonXPosition, exitButton.getY());
}
}

// Timer-based update for animations
@Override
public void actionPerformed(ActionEvent e) {
repaint();
}

// Main method to run the Game Menu
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> {
new GameMenu();
});
}
}
