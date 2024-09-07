import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

    private JButton btnReceitas;
    private JButton btnEstoque;
    private JButton btnDisponiveis;
    private JButton btnPedidos;

    public MenuPrincipal() {

        setTitle("Confeitaria - Menu Principal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));  // 4 ln, 1 col, espaço de 10px

        // Botões
        btnReceitas = new JButton("Cadastro de Produtos");
        btnEstoque = new JButton("Controle de Estoque");
        btnDisponiveis = new JButton("Resultados");
        btnPedidos = new JButton("Pedidos");

        add(btnReceitas);
        add(btnEstoque);
        add(btnDisponiveis);
        add(btnPedidos);

        // Os botões são para quando a usuária clicar levar até a tela escolhida

        btnReceitas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaReceitas();
            }
        });


        btnEstoque.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GerenciarEstoqueTela();
            }
        });


        btnDisponiveis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ControleCustosRecebimentosTela();
            }
        });


        btnPedidos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PedidosTela();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new MenuPrincipal();
    }
}
