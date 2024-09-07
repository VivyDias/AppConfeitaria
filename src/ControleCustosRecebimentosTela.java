import ConexaoJDBC.ConexaoBD;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date; // Importa java.sql.Date
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ControleCustosRecebimentosTela extends JFrame {
    private JTextField txtCustoTotal;
    private JTextField txtRecebimentosAcumulados;
    private JTextField txtLucro;
    private JTextField txtDataInicial;
    private JTextField txtDataFinal;
    private JButton btnAtualizarCustosRecebimentos;
    private JButton btnVoltarMenu; // Botão para voltar ao menu principal

    public ControleCustosRecebimentosTela() {
        setTitle("Controle de Custos, Recebimentos e Lucro");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtCustoTotal = new JTextField(10);
        txtCustoTotal.setEditable(false);  // Campo não editável
        txtRecebimentosAcumulados = new JTextField(10);
        txtRecebimentosAcumulados.setEditable(false);  // Campo não editável
        txtLucro = new JTextField(10);
        txtLucro.setEditable(false);  // Campo não editável
        txtDataInicial = new JTextField(10);
        txtDataFinal = new JTextField(10);
        btnAtualizarCustosRecebimentos = new JButton("Atualizar Custos e Recebimentos");

        // Botão para voltar ao menu principal
        btnVoltarMenu = new JButton("Voltar ao Menu Principal");

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        panel.add(new JLabel("Data Inicial (yyyy-mm-dd):"));
        panel.add(txtDataInicial);
        panel.add(new JLabel("Data Final (yyyy-mm-dd):"));
        panel.add(txtDataFinal);
        panel.add(new JLabel("Custo Total:"));
        panel.add(txtCustoTotal);
        panel.add(new JLabel("Recebimentos Acumulados:"));
        panel.add(txtRecebimentosAcumulados);
        panel.add(new JLabel("Lucro:"));
        panel.add(txtLucro);
        panel.add(btnAtualizarCustosRecebimentos);
        panel.add(btnVoltarMenu); // Adiciona o botão no painel

        btnAtualizarCustosRecebimentos.addActionListener(e -> atualizarCustosRecebimentos());

        // Ação do botão para voltar ao menu principal
        btnVoltarMenu.addActionListener(e -> {
            new MenuPrincipal(); // Abre a tela do menu principal
            dispose(); // Fecha a tela atual
        });

        add(panel);
        setVisible(true);
    }

    private void atualizarCustosRecebimentos() {
        String dataInicialStr = txtDataInicial.getText();
        String dataFinalStr = txtDataFinal.getText();

        if (!validarDatas(dataInicialStr, dataFinalStr)) {
            JOptionPane.showMessageDialog(this, "Datas inválidas. Verifique o formato (yyyy-mm-dd).");
            return;
        }

        // Converte as strings para java.sql.Date
        Date dataInicial = Date.valueOf(dataInicialStr);
        Date dataFinal = Date.valueOf(dataFinalStr);

        try (Connection conn = ConexaoBD.conectar()) {
            // Calcular custo total dos produtos vendidos no intervalo de tempo
            String sqlCusto = "SELECT SUM(ip.quantidade * r.custo) AS custo_total " +
                    "FROM itens_pedido ip " +
                    "JOIN estoque r ON ip.id_produto = r.id " +
                    "JOIN pedidos p ON ip.id_pedido = p.id " +
                    "WHERE p.data_pedido BETWEEN ? AND ?";
            PreparedStatement stmtCusto = conn.prepareStatement(sqlCusto);
            stmtCusto.setDate(1, dataInicial);
            stmtCusto.setDate(2, dataFinal);
            ResultSet rsCusto = stmtCusto.executeQuery();

            BigDecimal custoTotal = BigDecimal.ZERO; // Inicializa com ZERO
            if (rsCusto.next()) {
                custoTotal = rsCusto.getBigDecimal("custo_total");
                if (custoTotal == null) {
                    custoTotal = BigDecimal.ZERO; // Garante que custoTotal não seja null
                }
            }

            // Calcular total de recebimentos no intervalo de tempo
            String sqlRecebimentos = "SELECT SUM(valor_total) AS recebimentos_acumulados " +
                    "FROM pedidos " +
                    "WHERE data_pedido BETWEEN ? AND ?";
            PreparedStatement stmtRecebimentos = conn.prepareStatement(sqlRecebimentos);
            stmtRecebimentos.setDate(1, dataInicial);
            stmtRecebimentos.setDate(2, dataFinal);
            ResultSet rsRecebimentos = stmtRecebimentos.executeQuery();

            BigDecimal recebimentosAcumulados = BigDecimal.ZERO; // Inicializa com ZERO
            if (rsRecebimentos.next()) {
                recebimentosAcumulados = rsRecebimentos.getBigDecimal("recebimentos_acumulados");
                if (recebimentosAcumulados == null) {
                    recebimentosAcumulados = BigDecimal.ZERO; // Garante que recebimentosAcumulados não seja null
                }
            }

            // Calcular lucro (Recebimentos - Custos)
            BigDecimal lucro = recebimentosAcumulados.subtract(custoTotal);

            // Atualizar campos na tela automaticamente com as informações do banco de dados
            txtCustoTotal.setText(custoTotal.toString());
            txtRecebimentosAcumulados.setText(recebimentosAcumulados.toString());
            txtLucro.setText(lucro.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao calcular custos e recebimentos.");
        }
    }

    private boolean validarDatas(String dataInicial, String dataFinal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            java.util.Date dataIni = sdf.parse(dataInicial);
            java.util.Date dataFin = sdf.parse(dataFinal);

            // Converter java.util.Date para java.sql.Date para comparação
            Date dataIniSql = new Date(dataIni.getTime());
            Date dataFinSql = new Date(dataFin.getTime());

            return !dataIniSql.after(dataFinSql);
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        new ControleCustosRecebimentosTela();
    }
}
