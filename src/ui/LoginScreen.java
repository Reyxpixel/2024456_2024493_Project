package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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
        setLocationRelativeTo(null);

        loadBackgroundImage();
        loadLogoImage();
        loadEyeImage();

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JPanel topBanner = createTopBanner();
        backgroundPanel.add(topBanner);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        backgroundPanel.add(centerWrapper);
        
        TranslucentPanel contentPanel = new TranslucentPanel();
        contentPanel.setLayout(new GridBagLayout());
        
        updateContentPanelPadding(contentPanel);

        addFormContent(contentPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        int insets = Math.max(20, Math.min(getWidth(), getHeight()) / 25);
        gbc.insets = new Insets(insets, insets, insets, insets);
        centerWrapper.add(contentPanel, gbc);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int bannerHeight = (int)(getHeight() * 0.12);
                topBanner.setBounds(0, 0, getWidth(), bannerHeight);
                
                int availableHeight = getHeight() - bannerHeight;
                int contentHeight = contentPanel.getPreferredSize().height;
                int topInset = (availableHeight - contentHeight) / 2;
                int bottomInset = topInset;
                int sideInset = Math.max(20, Math.min(getWidth(), getHeight()) / 25);
                
                GridBagConstraints updatedGbc = new GridBagConstraints();
                updatedGbc.gridx = 0;
                updatedGbc.gridy = 0;
                updatedGbc.insets = new Insets(Math.max(topInset, 20), sideInset, Math.max(bottomInset, 20), sideInset);
                centerWrapper.remove(contentPanel);
                centerWrapper.add(contentPanel, updatedGbc);
                
                centerWrapper.setBounds(0, 0, getWidth(), getHeight());
                updateContentPanelPadding(contentPanel);
                topBanner.revalidate();
                topBanner.repaint();
                centerWrapper.revalidate();
                centerWrapper.repaint();
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
        
        int initialBannerHeight = (int)(getHeight() * 0.12);
        topBanner.setBounds(0, 0, getWidth(), initialBannerHeight);
        
        int availableHeight = getHeight() - initialBannerHeight;
        int contentHeight = contentPanel.getPreferredSize().height;
        int topInset = (availableHeight - contentHeight) / 2;
        int bottomInset = topInset;
        int sideInset = Math.max(20, Math.min(getWidth(), getHeight()) / 25);
        gbc.insets = new Insets(Math.max(topInset, 20), sideInset, Math.max(bottomInset, 20), sideInset);
        centerWrapper.remove(contentPanel);
        centerWrapper.add(contentPanel, gbc);
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
            File eyeShowFile = new File("src/ui/eye-svgrepo-com.png");
            if (!eyeShowFile.exists()) {
                eyeShowFile = new File("ui/eye-svgrepo-com.png");
            }
            if (eyeShowFile.exists()) {
                eyeShowIcon = ImageIO.read(eyeShowFile);
            }
            
            File eyeHideFile = new File("src/ui/eye-slash-svgrepo-com.png");
            if (!eyeHideFile.exists()) {
                eyeHideFile = new File("ui/eye-slash-svgrepo-com.png");
            }
            if (eyeHideFile.exists()) {
                eyeHideIcon = ImageIO.read(eyeHideFile);
            }
        } catch (Exception e) {
            System.err.println("Could not load eye icons: " + e.getMessage());
            eyeShowIcon = null;
            eyeHideIcon = null;
        }
    }

    private JPanel createTopBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("ERP Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(40, 40, 40));
        banner.add(titleLabel, BorderLayout.WEST);

        if (logoImage != null) {
            JLabel logoLabel = new JLabel() {
                @Override
                public Dimension getPreferredSize() {
                    int maxHeight = getParent() != null ? (int)(getParent().getHeight() * 1.0) : 80;
                    int originalWidth = logoImage.getWidth(null);
                    int originalHeight = logoImage.getHeight(null);
                    if (originalWidth > 0 && originalHeight > 0) {
                        int logoHeight = maxHeight;
                        int logoWidth = (int) ((double) originalWidth / originalHeight * logoHeight);
                        int availableWidth = getParent() != null ? (int)(getParent().getWidth() * 0.60) : 300;
                        if (logoWidth > availableWidth) {
                            logoWidth = availableWidth;
                            logoHeight = (int) ((double) availableWidth / originalWidth * originalHeight);
                        }
                        return new Dimension(logoWidth, logoHeight);
                    }
                    return new Dimension(300, 80);
                }
                
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (logoImage != null) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        int maxHeight = (int)(getHeight() * 1.0);
                        int originalWidth = logoImage.getWidth(null);
                        int originalHeight = logoImage.getHeight(null);
                        if (originalWidth > 0 && originalHeight > 0) {
                            int logoWidth = (int) ((double) originalWidth / originalHeight * maxHeight);
                            int logoHeight = maxHeight;
                            
                            int availableWidth = (int)(getWidth() * 0.60);
                            if (logoWidth > availableWidth) {
                                logoHeight = (int) ((double) availableWidth / originalWidth * originalHeight);
                                logoWidth = availableWidth;
                            }
                            
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
            fallbackLabel.setForeground(new Color(40, 40, 40));
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 0, 30, 0);
        JLabel heading = new JLabel("SIGN IN") {
            @Override
            public Dimension getPreferredSize() {
                int fontSize = Math.max(18, Math.min(getParent() != null ? getParent().getWidth() : 400, 600) / 25);
                Font font = new Font("Segoe UI", Font.BOLD, fontSize);
                FontMetrics fm = getFontMetrics(font);
                int textHeight = fm.getAscent() + fm.getDescent();
                return new Dimension(super.getPreferredSize().width, textHeight + 10);
            }
            
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                int fontSize = Math.max(18, Math.min(getWidth() > 0 ? getWidth() : 400, 600) / 25);
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        usernameField = new RoundedTextField(25);
        container.add(usernameField, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 30, 0);
        passwordField = new RoundedPasswordFieldWithEye(25);
        container.add(passwordField, gbc);

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
        loginButton.setPreferredSize(new Dimension(160, 42));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        container.add(loginButton, gbc);

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

    private class RoundedTextField extends JTextField {
        private int cornerRadius = 12;
        
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
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
            int baseWidth = getWidth() > 0 ? getWidth() : 400;
            int height = Math.max(35, Math.min(baseWidth, 600) / 15);
            return new Dimension(d.width, height);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            g2.setColor(Color.WHITE);
            g2.fill(roundedRect);
            
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(roundedRect);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    private class RoundedPasswordFieldWithEye extends JPasswordField {
        private int cornerRadius = 12;
        private Rectangle eyeButtonBounds;
        
        public RoundedPasswordFieldWithEye(int columns) {
            super(columns);
            setOpaque(false);
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
            setBorder(new EmptyBorder(padding, padding * 2, padding, padding * 5));
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
            int baseWidth = getWidth() > 0 ? getWidth() : 400;
            int height = Math.max(35, Math.min(baseWidth, 600) / 15);
            return new Dimension(d.width, height);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            
            g2.setColor(Color.WHITE);
            g2.fill(roundedRect);
            
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(roundedRect);
            
            g2.dispose();
            super.paintComponent(g);
            
            BufferedImage currentEyeIcon = passwordVisible ? eyeHideIcon : eyeShowIcon;
            if (currentEyeIcon != null) {
                g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                
                int iconSize = Math.max(20, Math.min(getHeight() - 8, getWidth() / 18));
                int margin = Math.max(12, iconSize / 2);
                int x = getWidth() - iconSize - margin;
                int y = (getHeight() - iconSize) / 2;
                eyeButtonBounds = new Rectangle(x, y, iconSize, iconSize);
                
                g2.drawImage(currentEyeIcon, x, y, iconSize, iconSize, null);
                g2.dispose();
            }
        }
    }
    
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
        private static final Color BACKGROUND = new Color(255, 255, 255, 180);

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
