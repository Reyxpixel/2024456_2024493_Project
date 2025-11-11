package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginScreen extends JFrame {
    private RoundedTextField usernameField;
    private RoundedPasswordFieldWithEye passwordField;
    private JLabel changePasswordLabel;
    private boolean passwordVisible = false;
    private Image backgroundImage;
    private Image logoImage;
    private BufferedImage eyeShowIcon;
    private BufferedImage eyeHideIcon;

    public LoginScreen() {
        setTitle("IIITD ERP Portal - Login");
        setSize(600, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        loadBackgroundImage();
        loadLogoImage();
        loadEyeImage();

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null); // Use null layout for absolute positioning
        setContentPane(backgroundPanel);

        // Create banner panel that overlays the background
        JPanel bannerPanel = createBannerPanel();
        backgroundPanel.add(bannerPanel);
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        backgroundPanel.add(centerWrapper);
        
        TranslucentPanel contentPanel = new TranslucentPanel();
        contentPanel.setLayout(new GridBagLayout());
        
        // Responsive padding - will be updated on resize
        updateContentPanelPadding(contentPanel);

        addFormContent(contentPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Responsive insets
        int insets = Math.max(20, Math.min(getWidth(), getHeight()) / 25);
        gbc.insets = new Insets(insets, insets, insets, insets);
        centerWrapper.add(contentPanel, gbc);
        
        // Single component listener to handle all resizing - make everything responsive
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int bannerHeight = (int)(getHeight() * 0.12); // 12% of window height
                bannerPanel.setBounds(0, 0, getWidth(), bannerHeight);
                centerWrapper.setBounds(0, 0, getWidth(), getHeight());
                updateContentPanelPadding(contentPanel);
                bannerPanel.revalidate();
                bannerPanel.repaint();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        
        // Set initial bounds - responsive sizing
        int initialBannerHeight = (int)(getHeight() * 0.12);
        bannerPanel.setBounds(0, 0, getWidth(), initialBannerHeight);
        centerWrapper.setBounds(0, 0, getWidth(), getHeight());
    }

    private void loadBackgroundImage() {
        try {
            File imgFile = new File("src/ui/iiitdbackground.png");
            if (!imgFile.exists()) {
                imgFile = new File("ui/iiitdbackground.png");
            }
            if (imgFile.exists()) {
                backgroundImage = ImageIO.read(imgFile);
            } else {
                System.err.println("Background image not found. Using fallback gradient.");
                backgroundImage = null;
            }
        } catch (IOException e) {
            System.err.println("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void loadLogoImage() {
        try {
            File logoFile = new File("src/ui/style3colorlarge.png");
            if (!logoFile.exists()) {
                logoFile = new File("ui/style3colorlarge.png");
            }
            if (logoFile.exists()) {
                logoImage = ImageIO.read(logoFile);
            } else {
                System.err.println("Logo image not found.");
                logoImage = null;
            }
        } catch (IOException e) {
            System.err.println("Could not load logo image: " + e.getMessage());
            logoImage = null;
        }
    }
    
    private void loadEyeImage() {
        try {
            // Load eye show icon
            File eyeShowFile = new File("src/ui/eye-svgrepo-com.svg");
            if (!eyeShowFile.exists()) {
                eyeShowFile = new File("ui/eye-svgrepo-com.svg");
            }
            if (eyeShowFile.exists()) {
                eyeShowIcon = renderSVGIcon(eyeShowFile, 20);
            }
            
            // Load eye hide icon
            File eyeHideFile = new File("src/ui/eye-slash-svgrepo-com.svg");
            if (!eyeHideFile.exists()) {
                eyeHideFile = new File("ui/eye-slash-svgrepo-com.svg");
            }
            if (eyeHideFile.exists()) {
                eyeHideIcon = renderSVGIcon(eyeHideFile, 20);
            }
        } catch (Exception e) {
            System.err.println("Could not load eye icons: " + e.getMessage());
            eyeShowIcon = null;
            eyeHideIcon = null;
        }
    }
    
    private BufferedImage renderSVGIcon(File svgFile, int size) {
        try {
            // Read SVG file
            String svgContent = new String(java.nio.file.Files.readAllBytes(svgFile.toPath()));
            
            // Check if it's the slash version
            boolean isSlash = svgFile.getName().contains("slash");
            
            // Create buffered image
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            // Set black color
            g2d.setColor(Color.BLACK);
            
            // Parse and render SVG paths properly
            renderSVGPaths(g2d, svgContent, size, isSlash);
            
            g2d.dispose();
            return img;
        } catch (Exception e) {
            System.err.println("Error rendering SVG: " + e.getMessage());
            e.printStackTrace();
            // Fallback: create a simple eye icon
            return createFallbackEyeIcon(size);
        }
    }
    
    private void renderSVGPaths(Graphics2D g2, String svgContent, int size, boolean isSlash) {
        // Scale factor: SVG viewBox is 36x36
        double scale = (double) size / 36.0;
        g2.scale(scale, scale);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        if (isSlash) {
            // Eye-slash icon - render based on SVG path data
            // Draw the eye shape (outer ellipse)
            g2.drawOval(2, 6, 32, 20);
            
            // Draw inner circle (pupil area) - smaller
            g2.fillOval(14, 12, 8, 8);
            
            // Draw diagonal slash line through the eye
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Main diagonal line
            g2.drawLine(3, 3, 33, 33);
            // Additional line for thickness
            g2.drawLine(4, 4, 32, 32);
        } else {
            // Eye icon - render based on SVG path data
            // Outer eye shape (ellipse) - from path d attribute
            // The path creates an ellipse shape
            g2.drawOval(2, 6, 32, 20);
            
            // Inner circle (pupil) - from circle element
            // cx="18.09" cy="18.03" r="6.86" means center at (18.09, 18.03) with radius 6.86
            int centerX = 18;
            int centerY = 18;
            int radius = 7;
            g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
    }
    
    private BufferedImage createFallbackEyeIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Draw simple eye shape
        int centerX = size / 2;
        int centerY = size / 2;
        g2d.drawOval(centerX - size/3, centerY - size/4, size*2/3, size/2);
        g2d.fillOval(centerX - 2, centerY - 2, 4, 4);
        
        g2d.dispose();
        return img;
    }

    private JPanel createBannerPanel() {
        // Create banner with proper layering: translucent background, then text and logo on top
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw translucent white background - overlays the image
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // 70% opacity
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        banner.setOpaque(false); // Critical: must be non-opaque to see through
        banner.setBorder(new EmptyBorder(15, 30, 15, 30)); // Add padding so content isn't cut off

        // "ERP Login" text on the left
        JLabel titleLabel = new JLabel("ERP Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(45, 52, 54));
        banner.add(titleLabel, BorderLayout.WEST);

        // Logo on the right - use ImageIcon for proper display
        if (logoImage != null) {
            JLabel logoLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (logoImage != null) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Calculate size based on available space - fully responsive
                        int maxHeight = (int)(getHeight() * 0.85); // 85% of banner height
                        int originalWidth = logoImage.getWidth(null);
                        int originalHeight = logoImage.getHeight(null);
                        if (originalWidth > 0 && originalHeight > 0) {
                            int logoWidth = (int) ((double) originalWidth / originalHeight * maxHeight);
                            int logoHeight = maxHeight;
                            
                            // Ensure it doesn't exceed available width
                            int availableWidth = (int)(getWidth() * 0.45); // Max 45% of banner width
                            if (logoWidth > availableWidth) {
                                logoHeight = (int) ((double) availableWidth / originalWidth * originalHeight);
                                logoWidth = availableWidth;
                            }
                            
                            // Center vertically, align right
                            int x = getWidth() - logoWidth;
                            int y = (getHeight() - logoHeight) / 2;
                            g2.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
                        }
                        g2.dispose();
                    }
                }
            };
            logoLabel.setOpaque(false);
            banner.add(logoLabel, BorderLayout.EAST);
        } else {
            JLabel fallbackLabel = new JLabel("IIIT-Delhi");
            fallbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            banner.add(fallbackLabel, BorderLayout.EAST);
        }

        return banner;
    }
    
    private void updateContentPanelPadding(JPanel contentPanel) {
        int padding = Math.max(20, Math.min(getWidth(), getHeight()) / 20);
        contentPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));
    }

    private void addFormContent(JPanel container) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading - responsive font
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 30, 0);
        JLabel heading = new JLabel("SIGN IN") {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Responsive font size
                int fontSize = Math.max(18, Math.min(getWidth(), getHeight()) / 25);
                g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
                g2.setColor(new Color(40, 40, 40));
                
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2;
                
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        heading.setOpaque(false);
        container.add(heading, gbc);

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(60, 60, 60));
        container.add(usernameLabel, gbc);

        // Username field - responsive height
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        usernameField = new RoundedTextField(25);
        // Height will be set responsively in the field itself
        container.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordLabel.setForeground(new Color(60, 60, 60));
        container.add(passwordLabel, gbc);

        // Password field with eye icon inside - responsive height
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 30, 0);
        passwordField = new RoundedPasswordFieldWithEye(25);
        // Height will be set responsively in the field itself
        container.add(passwordField, gbc);

        // Login button - smaller size
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setPreferredSize(new Dimension(160, 42)); // Smaller button
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        container.add(loginButton, gbc);

        // Change password (not underlined, centered)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        changePasswordLabel = new JLabel("Change Password");
        changePasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        changePasswordLabel.setForeground(new Color(33, 150, 243));
        changePasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changePasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(changePasswordLabel, gbc);

        loginButton.addActionListener(e -> handleLogin());
        changePasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        LoginScreen.this,
                        "Change password functionality will be implemented soon."
                );
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                changePasswordLabel.setForeground(new Color(13, 110, 207));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                changePasswordLabel.setForeground(new Color(33, 150, 243));
            }
        });
    }
    
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
        passwordField.repaint();
    }

    // Rounded Text Field
    private class RoundedTextField extends JTextField {
        private int cornerRadius = 12;
        
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            // Responsive font size - will be updated on resize
            updateFontAndPadding();
        }
        
        private void updateFontAndPadding() {
            int fontSize = Math.max(12, Math.min(getWidth() > 0 ? getWidth() : 400, 600) / 40);
            setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            int padding = Math.max(8, Math.min(getWidth() > 0 ? getWidth() : 400, 600) / 50);
            setBorder(new EmptyBorder(padding, padding * 2, padding, padding * 2));
        }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            if (width > 0) {
                updateFontAndPadding();
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            // Responsive height
            int baseWidth = getWidth() > 0 ? getWidth() : 400;
            int height = Math.max(35, Math.min(baseWidth, 600) / 15);
            return new Dimension(d.width, height);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Draw rounded rectangle background
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            // Fill background
            g2.setColor(Color.WHITE);
            g2.fill(roundedRect);
            
            // Draw border with better anti-aliasing
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(roundedRect);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Rounded Password Field with Eye Icon Inside
    private class RoundedPasswordFieldWithEye extends JPasswordField {
        private int cornerRadius = 12;
        private Rectangle eyeButtonBounds;
        
        public RoundedPasswordFieldWithEye(int columns) {
            super(columns);
            setOpaque(false);
            // Responsive font size - will be updated on resize
            updateFontAndPadding();
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (eyeButtonBounds != null && eyeButtonBounds.contains(e.getPoint())) {
                        togglePasswordVisibility();
                    }
                }
            });
            
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (eyeButtonBounds != null && eyeButtonBounds.contains(e.getPoint())) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                        setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        }
        
        private void updateFontAndPadding() {
            int fontSize = Math.max(12, Math.min(getWidth() > 0 ? getWidth() : 400, 600) / 40);
            setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            int padding = Math.max(8, Math.min(getWidth() > 0 ? getWidth() : 400, 600) / 50);
            setBorder(new EmptyBorder(padding, padding * 2, padding, padding * 5)); // Extra right padding for eye icon
        }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            if (width > 0) {
                updateFontAndPadding();
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            // Responsive height
            int baseWidth = getWidth() > 0 ? getWidth() : 400;
            int height = Math.max(35, Math.min(baseWidth, 600) / 15);
            return new Dimension(d.width, height);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Draw rounded rectangle background
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            // Fill background
            g2.setColor(Color.WHITE);
            g2.fill(roundedRect);
            
            // Draw border with better anti-aliasing
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(roundedRect);
            
            // Draw text content
            g2.dispose();
            super.paintComponent(g);
            
            // Draw eye icon on top
            BufferedImage currentEyeIcon = passwordVisible ? eyeHideIcon : eyeShowIcon;
            if (currentEyeIcon != null) {
                g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Responsive icon size
                int iconSize = Math.max(16, Math.min(getHeight(), getWidth() / 20));
                int margin = Math.max(10, iconSize / 2);
                int x = getWidth() - iconSize - margin;
                int y = (getHeight() - iconSize) / 2;
                eyeButtonBounds = new Rectangle(x, y, iconSize, iconSize);
                
                g2.drawImage(currentEyeIcon, x, y, null);
                g2.dispose();
            }
        }
    }
    
    // Rounded Button
    private class RoundedButton extends JButton {
        private int cornerRadius = 25;
        
        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fill(roundedRect);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = AuthService.login(username, password);

        if (user != null) {
            openDashboard(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(135, 206, 250),
                        getWidth(), getHeight(), new Color(70, 130, 180)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private static class TranslucentPanel extends JPanel {
        private static final Color BACKGROUND = new Color(255, 255, 255, 180); // More translucent

        private TranslucentPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setColor(BACKGROUND);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Add subtle border
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private void openDashboard(User user) {
        String role = user.getRole().toLowerCase();

        if (role.equals("student")) {
            new StudentDashboard(user.getUsername()).setVisible(true);
        } else if (role.equals("instructor")) {
            new InstructorDashboard(user.getUsername()).setVisible(true);
        } else if (role.equals("admin")) {
            new AdminDashboard(user.getUsername()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Unknown role: " + role);
        }
    }
}
