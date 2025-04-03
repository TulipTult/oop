import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private boolean maintainRatio = true;

    public ImagePanel(String imagePath) {
        loadImage(imagePath);
        // Default size when no image is loaded
        setPreferredSize(new Dimension(400, 400)); // Increased from 300x300
    }

    private void loadImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            image = null;
            return;
        }

        String[] possiblePaths = {
            imagePath,
            "src/" + imagePath,
            "bin/" + imagePath,
            "../" + imagePath,
            "." + File.separator + imagePath,
            ".." + File.separator + imagePath,
            System.getProperty("user.dir") + File.separator + imagePath
        };

        for (String path : possiblePaths) {
            try {
                File file = new File(path);
                System.out.println("Attempting to load image from: " + file.getAbsolutePath());
                
                if (file.exists()) {
                    image = ImageIO.read(file);
                    if (image != null) {
                        System.out.println("Successfully loaded image from: " + file.getAbsolutePath());
                        // Set panel size based on image dimensions
                        updatePanelSize();
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to load image from: " + path);
            }
        }
        
        System.err.println("Could not load image from any path: " + imagePath);
        image = null;
    }
    
    private void updatePanelSize() {
        if (image != null) {
            int maxWidth = 500; // Increased max width from 400
            int maxHeight = 500; // Increased max height from 400
            
            int width = Math.min(image.getWidth(), maxWidth);
            int height = Math.min(image.getHeight(), maxHeight);
            
            // Maintain aspect ratio if needed
            if (maintainRatio && (image.getWidth() > maxWidth || image.getHeight() > maxHeight)) {
                double ratio = (double) image.getWidth() / image.getHeight();
                if (image.getWidth() > image.getHeight()) {
                    width = maxWidth;
                    height = (int) (width / ratio);
                } else {
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
            }
            
            setPreferredSize(new Dimension(width, height));
            revalidate();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // Calculate scale to fit within the panel
            double widthScale = (double) getWidth() / image.getWidth();
            double heightScale = (double) getHeight() / image.getHeight();
            double scale = Math.min(widthScale, heightScale); // Use smaller scale to ensure it fits
            
            // Calculate dimensions that maintain aspect ratio
            int scaledWidth = (int) (image.getWidth() * scale);
            int scaledHeight = (int) (image.getHeight() * scale);
            
            // Center the image in the panel
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;
            
            // Draw the scaled image
            g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
        } else {
            g.drawString("Image not available", 50, 100);
        }
    }
}
