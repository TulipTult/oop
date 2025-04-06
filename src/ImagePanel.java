import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImagePanel extends JPanel {
    // bufferdimage init
    private BufferedImage image;
    private boolean maintainRatio = true;

    // Create a panel with an image
    public ImagePanel(String imagePath) {
        loadImage(imagePath);
        // How big to make the panel if no image
        setPreferredSize(new Dimension(400, 400));
    }

    // Try to find and load the image
    private void loadImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            image = null;
            return;
        }

        // Try diff directories to find image 
        String[] possiblePaths = {
            imagePath,
            "src/" + imagePath,
            "bin/" + imagePath,
            "../" + imagePath,
            "." + File.separator + imagePath,
            ".." + File.separator + imagePath,
            System.getProperty("user.dir") + File.separator + imagePath
        };

        // find path
        for (String path : possiblePaths) {
            try {
                File file = new File(path);
                System.out.println("Looking for image at: " + file.getAbsolutePath());
                
                if (file.exists()) {
                    image = ImageIO.read(file);
                    if (image != null) {
                        System.out.println("Found it! Loaded image from: " + file.getAbsolutePath());
                        // Fix the panel size based on the image
                        updatePanelSize();
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println("Couldn't load image from: " + path);
            }
        }
        
        System.err.println("No luck finding the image: " + imagePath);
        image = null;
    }
    
    // panel size change to-fit image
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

    // draw the image in our panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // calc scale to fit within the panel
            double widthScale = (double) getWidth() / image.getWidth();
            double heightScale = (double) getHeight() / image.getHeight();
            double scale = Math.min(widthScale, heightScale); // smaller scale to ensure it fits
            
            // Calc dimensions that maintain aspect ratio
            int scaledWidth = (int) (image.getWidth() * scale);
            int scaledHeight = (int) (image.getHeight() * scale);
            
            // Center the image in the panel
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;
            
            // draw the moded image
            g.drawImage(image, x, y, scaledWidth, scaledHeight, this);
        } else {
            g.drawString("Image not available", 50, 100);
        }
    }
}
