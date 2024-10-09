package solar;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SistemaSolar extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 50);

    private List<Planeta> planetas;
    private Timer timer;
    private double escala;
    private long tempoSimulacao;
    private static long TEMPO_REAL_POR_SEGUNDO = 86400; // 1 dia p/ segundo na simulação
    private JButton btnVelocidade;
    private int velocidadeAtual = 1;
    private JTextArea infoTextArea;

    public SistemaSolar() {
        setTitle("Sistema Solar");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());

        escala = Math.min(WIDTH, HEIGHT) / 1000.0;
        tempoSimulacao = System.currentTimeMillis() / 1000;
        inicializarPlanetas();

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(BACKGROUND_COLOR);
        btnVelocidade = new JButton("1x");
        btnVelocidade.setFont(new Font("Arial", Font.BOLD, 14));
        btnVelocidade.setForeground(Color.WHITE);
        btnVelocidade.setBackground(new Color(0, 100, 200));
        btnVelocidade.setBorder(BorderFactory.createRaisedBevelBorder());
        btnVelocidade.setFocusPainted(false);
        btnVelocidade.addActionListener(e -> mudarVelocidade());
        controlPanel.add(btnVelocidade);
        add(controlPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharSistemaSolar((Graphics2D) g);
            }
        };
        panel.setBackground(BACKGROUND_COLOR);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                verificarCliquePlaneta(e.getX(), e.getY());
            }
        });
        add(panel, BorderLayout.CENTER);

        infoTextArea = new JTextArea(10, 50);
        infoTextArea.setEditable(false);
        infoTextArea.setBackground(new Color(30, 30, 70));
        infoTextArea.setForeground(Color.WHITE);
        infoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        add(scrollPane, BorderLayout.EAST);

        timer = new Timer(16, e -> {
            moverPlanetas();
            tempoSimulacao += TEMPO_REAL_POR_SEGUNDO / 60;
            repaint();
        });
        timer.start();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                escala = Math.min(getWidth(), getHeight()) / 1000.0;
                repaint();
            }
        });
    }

    private void mudarVelocidade() {
        switch (velocidadeAtual) {
            case 1:
                velocidadeAtual = 10;
                TEMPO_REAL_POR_SEGUNDO = 86400 * 10;
                break;
            case 10:
                velocidadeAtual = 50;
                TEMPO_REAL_POR_SEGUNDO = 86400 * 50;
                break;
            case 50:
                velocidadeAtual = 100;
                TEMPO_REAL_POR_SEGUNDO = 86400 * 100;
                break;
            case 100:
                velocidadeAtual = 1;
                TEMPO_REAL_POR_SEGUNDO = 86400;
                break;
        }
        btnVelocidade.setText(velocidadeAtual + "x");
    }

    private void inicializarPlanetas() {
        planetas = new ArrayList<>();
        planetas.add(new Planeta("Mercúrio", 10, 60, 87.97, Color.GRAY, 0.387, 47.4));
        planetas.add(new Planeta("Vênus", 15, 100, 224.7, Color.ORANGE, 0.723, 35.0));
        planetas.add(new Planeta("Terra", 16, 140, 365.26, Color.BLUE, 1.0, 29.8));
        planetas.add(new Planeta("Marte", 12, 180, 686.98, Color.RED, 1.524, 24.1));
        planetas.add(new Planeta("Júpiter", 30, 240, 4332.59, Color.ORANGE.darker(), 5.203, 13.1));
        planetas.add(new Planeta("Saturno", 28, 300, 10759.22, Color.YELLOW.darker(), 9.572, 9.7));
        planetas.add(new Planeta("Urano", 22, 360, 30688.5, Color.CYAN, 19.16, 6.8));
        planetas.add(new Planeta("Netuno", 21, 420, 60195, Color.BLUE.darker(), 30.18, 5.4));


        for (Planeta planeta : planetas) {
            planeta.calcularPosicaoInicial(tempoSimulacao);
        }
    }

    private void moverPlanetas() {
        for (Planeta planeta : planetas) {
            planeta.mover(tempoSimulacao);
        }
    }

    private void desenharSistemaSolar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String tempoFormatado = sdf.format(new Date(tempoSimulacao * 1000));
        g.drawString("Tempo de Simulação: " + tempoFormatado, 10, 30);


        int sunSize = (int) (60 * escala);
        g.setColor(Color.YELLOW);
        g.fill(new Ellipse2D.Double(centerX - sunSize / 2, centerY - sunSize / 2, sunSize, sunSize));
        g.setColor(Color.WHITE);
        g.drawString("Sol", centerX - 10, centerY + sunSize / 2 + 15);

        for (Planeta planeta : planetas) {
            planeta.desenhar(g, centerX, centerY, escala);
        }
    }

    private void verificarCliquePlaneta(int x, int y) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (Planeta planeta : planetas) {
            int scaledDistance = (int) (planeta.distancia * escala);
            int planetX = (int) (centerX + scaledDistance * Math.cos(planeta.angulo));
            int planetY = (int) (centerY + scaledDistance * Math.sin(planeta.angulo));
            int scaledSize = (int) (planeta.tamanho * escala);

            if (x >= planetX - scaledSize / 2 && x <= planetX + scaledSize / 2 &&
                y >= planetY - scaledSize / 2 && y <= planetY + scaledSize / 2) {
                exibirInformacoesPlaneta(planeta);
                break;
            }
        }
    }

    private void exibirInformacoesPlaneta(Planeta planeta) {
        StringBuilder info = new StringBuilder();
        info.append("Informações do Planeta: ").append(planeta.nome).append("\n\n");
        info.append("Tamanho: ").append(planeta.tamanho).append(" unidades\n");
        //info.append("Distância do Sol: ").append(String.format("%.2f", planeta.distancia * escala)).append(" pixels\n");
        info.append("Período orbital: ").append(String.format("%.2f", planeta.periodo)).append(" dias terrestres\n");
        info.append("Distância do Sol: ").append(String.format("%.3f", planeta.distanciaReal)).append(" UA\n");
        info.append("Velocidade orbital: ").append(String.format("%.2f", planeta.velocidadeOrbital)).append(" km/s\n");
        double x = planeta.distancia * Math.cos(planeta.angulo);
        double y = planeta.distancia * Math.sin(planeta.angulo);
        info.append("Posição atual (x, y): (").append(String.format("%.2f", x)).append(", ").append(String.format("%.2f", y)).append(")\n");
        double vx = -planeta.velocidadeOrbital * Math.sin(planeta.angulo);
        double vy = planeta.velocidadeOrbital * Math.cos(planeta.angulo);
        info.append("Velocidade atual (vx, vy): (").append(String.format("%.2f", vx)).append(", ").append(String.format("%.2f", vy)).append(") km/s\n");
        double tempoOrbitalDecorrido = (planeta.angulo / (2 * Math.PI)) * planeta.periodo;
        info.append("Tempo orbital decorrido: ").append(String.format("%.2f", tempoOrbitalDecorrido)).append(" dias\n");
        double deltaV = calcularDeltaV(planeta);
        info.append("Delta-V para transferência de Hohmann: ").append(String.format("%.2f", deltaV)).append(" km/s\n");
        double tempoRestante = planeta.periodo - tempoOrbitalDecorrido;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dataProximaOrbita = new Date((tempoSimulacao + (long)(tempoRestante * 24 * 3600)) * 1000);
        info.append("Próxima órbita completa: ").append(sdf.format(dataProximaOrbita)).append("\n");

       infoTextArea.setText(info.toString());
    }

    private double calcularDeltaV(Planeta planeta) {
        // Assumindo uma transferência de Hohmann da Terra para o planeta alvo
        double r1 = 1.0; // Raio da órbita da Terra (1 UA)
        double r2 = planeta.distanciaReal; // Raio da órbita do planeta alvo
        double mu = 1.32712440018e20; // Parâmetro gravitacional padrão do Sol (m³/s²)

        // Converter UA para metros
        r1 *= 1.496e11;
        r2 *= 1.496e11;

        // Calcular velocidades
        double v1 = Math.sqrt(mu / r1);
        double v2 = Math.sqrt(mu / r2);
        double vTransfer1 = Math.sqrt(mu * (2 / r1 - 2 / (r1 + r2)));
        double vTransfer2 = Math.sqrt(mu * (2 / r2 - 2 / (r1 + r2)));

        // Calcular Delta-V total
        double deltaV1 = Math.abs(vTransfer1 - v1);
        double deltaV2 = Math.abs(v2 - vTransfer2);
        double deltaVTotal = deltaV1 + deltaV2;

        // Converter de m/s para km/s
        return deltaVTotal / 1000;
    }

    private class Planeta {
        String nome;
        int tamanho;
        int distancia;
        double periodo;
        Color cor;
        double angulo;
        double distanciaReal;
        double velocidadeOrbital;

        public Planeta(String nome, int tamanho, int distancia, double periodo, Color cor, double distanciaReal, double velocidadeOrbital) {
            this.nome = nome;
            this.tamanho = tamanho;
            this.distancia = distancia;
            this.periodo = periodo;
            this.cor = cor;
            this.angulo = 0;
            this.distanciaReal = distanciaReal;
            this.velocidadeOrbital = velocidadeOrbital;
        }

        public void calcularPosicaoInicial(long tempoInicial) {
            double velocidadeAngular = 2 * Math.PI / (periodo * 24 * 3600);
            angulo = (velocidadeAngular * tempoInicial) % (2 * Math.PI);
        }

        public void mover(long tempoSimulacao) {
            double velocidadeAngular = 2 * Math.PI / (periodo * 24 * 3600);
            angulo = (velocidadeAngular * tempoSimulacao) % (2 * Math.PI);
        }

        public void desenhar(Graphics2D g, int centerX, int centerY, double escala) {
            int scaledDistance = (int) (distancia * escala);
            int x = (int) (centerX + scaledDistance * Math.cos(angulo));
            int y = (int) (centerY + scaledDistance * Math.sin(angulo));
            int scaledSize = (int) (tamanho * escala);


            g.setColor(Color.GRAY);
            g.drawOval(centerX - scaledDistance, centerY - scaledDistance, scaledDistance * 2, scaledDistance * 2);


            g.setColor(cor);
            g.fillOval(x - scaledSize / 2, y - scaledSize / 2, scaledSize, scaledSize);

            g.setColor(Color.WHITE);
            g.drawString(nome, x + scaledSize / 2 + 5, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaSolar sistemaSolar = new SistemaSolar();
            sistemaSolar.setVisible(true);
        });
    }
}