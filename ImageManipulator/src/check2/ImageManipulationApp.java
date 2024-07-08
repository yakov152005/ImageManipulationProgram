package check2;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static utilz.Constants.SizeImage.*;
import static utilz.Constants.Manipulation.*;
import static utilz.Constants.ColorRGB.*;

public class ImageManipulationApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage displayedImage;
    private JLabel imageLabel;
    private String selectedEffect;
    private int splitX;

    public ImageManipulationApp() {
        initUI();
    }

    private void initUI() {
        this.splitX  = -1;
        this.selectedEffect = "";

        setTitle("Image Manipulation App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                splitX = e.getX();
                applyEffect();
            }
        });

        JPanel buttonPanel = new JPanel();
        String[] effects = {"בחר אפקט","Black-White", "Grayscale", "Posterize", "Tint", "Color Shift Right",
                "Color Shift Left", "Mirror", "Pixelate", "Show Borders", "Eliminate Red",
                "Eliminate Green", "Eliminate Blue", "Negative", "Contrast", "Sepia", "Lighter",
                "Darker", "Vignette", "Add Noise", "Solarize", "Vintage"};


        JButton openButton = new JButton("Open Image");
        openButton.addActionListener(e -> openImage());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(openButton, BorderLayout.WEST);

        JComboBox<String> effectComboBox = new JComboBox<>(effects);
        effectComboBox.addActionListener(e -> {
            selectedEffect = (String) effectComboBox.getSelectedItem();
            applyEffect();
        });
        topPanel.add(effectComboBox, BorderLayout.CENTER);


        JButton saveButton = new JButton("Save Image");
        saveButton.addActionListener(e -> saveImage());
        topPanel.add(saveButton, BorderLayout.AFTER_LINE_ENDS);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                displayedImage = originalImage;
                imageLabel.setIcon(new ImageIcon(displayedImage));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    private void applyEffect() {
        if (originalImage == null || selectedEffect.isEmpty()) {
            return;
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                if (splitX != -1 && x < splitX) {
                    rgb = applyEffectToPixel(rgb, selectedEffect);
                }
                resultImage.setRGB(x, y, rgb);
            }
        }

        displayedImage = resultImage;
        imageLabel.setIcon(new ImageIcon(displayedImage));
    }


    private int applyEffectToPixel(int rgb, String effect) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        switch (effect) {
            case "Black-White":
                int gray = (r + g + b) / 3;
                return gray > 127 ? 0xffffff : 0x000000;
            case "Grayscale":
                gray = (r + g + b) / 3;
                return (gray << 16) | (gray << 8) | gray;
            case "Posterize":
                int numColors = 4;
                int colorStep = 256 / numColors;
                r = (r / colorStep) * colorStep;
                g = (g / colorStep) * colorStep;
                b = (b / colorStep) * colorStep;
                return (r << 16) | (g << 8) | b;
            case "Tint":
                Color tint = new Color(255, 100, 100, 50);
                r = (r + tint.getRed()) / 2;
                g = (g + tint.getGreen()) / 2;
                b = (b + tint.getBlue()) / 2;
                return (r << 16) | (g << 8) | b;
            case "Color Shift Right":
                return (g << 16) | (b << 8) | r;
            case "Color Shift Left":
                return (b << 16) | (r << 8) | g;
            case "Mirror":
                break;
            case "Pixelate":
                break;
            case "Show Borders":
                break;
            case "Eliminate Red":
                return (0 << 16) | (g << 8) | b;
            case "Eliminate Green":
                return (r << 16) | (0 << 8) | b;
            case "Eliminate Blue":
                return (r << 16) | (g << 8) | 0;
            case "Negative":
                return ((255 - r) << 16) | ((255 - g) << 8) | (255 - b);
            case "Contrast":
                float contrast = 1.2f;
                r = Math.min(255, Math.max(0, (int)(r * contrast)));
                g = Math.min(255, Math.max(0, (int)(g * contrast)));
                b = Math.min(255, Math.max(0, (int)(b * contrast)));
                return (r << 16) | (g << 8) | b;
            case "Sepia":
                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                r = Math.min(255, tr);
                g = Math.min(255, tg);
                b = Math.min(255, tb);
                return (r << 16) | (g << 8) | b;
            case "Lighter":
                float factor = 1.2f;
                r = Math.min(255, (int)(r * factor));
                g = Math.min(255, (int)(g * factor));
                b = Math.min(255, (int)(b * factor));
                return (r << 16) | (g << 8) | b;
            case "Darker":
                factor = 0.8f;
                r = (int)(r * factor);
                g = (int)(g * factor);
                b = (int)(b * factor);
                return (r << 16) | (g << 8) | b;
            case "Vignette":

                break;
            case "Add Noise":
                int noise = (int)(Math.random() * 64) - 32;
                r = Math.min(255, Math.max(0, r + noise));
                g = Math.min(255, Math.max(0, g + noise));
                b = Math.min(255, Math.max(0, b + noise));
                return (r << 16) | (g << 8) | b;
            case "Solarize":
                int threshold = 128;
                if (r > threshold) r = 255 - r;
                if (g > threshold) g = 255 - g;
                if (b > threshold) b = 255 - b;
                return (r << 16) | (g << 8) | b;
            case "Vintage":
                return applyEffectToPixel(applyEffectToPixel(rgb, "Add Noise"), "Sepia");
        }
        return rgb;
    }



    private void saveImage() {
        if (displayedImage == null) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Images", "jpg"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getPath();

                if (!filePath.toLowerCase().endsWith(".jpg")) {
                    file = new File(filePath + ".jpg");
                }

                ImageIO.write(displayedImage, "jpg", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageManipulationApp app = new ImageManipulationApp();
            app.setVisible(true);
        });
    }
}
