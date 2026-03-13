package bll;

import dal.PedidoCompraRepository;
import model.PedidoCompra;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoCompraService {

    private final PedidoCompraRepository repository;

    public PedidoCompraService(PedidoCompraRepository repository) {
        this.repository = repository;
    }

    public PedidoCompra criarPedido(PedidoCompra pedido) {

        if (pedido.getIdFornecedor() == null) {
            throw new RuntimeException("Pedido precisa de fornecedor.");
        }

        if (pedido.getIdAssistente() == null) {
            throw new RuntimeException("Pedido precisa de assistente responsável.");
        }

        return repository.save(pedido);
    }

    public List<PedidoCompra> listarTodos() {
        return repository.findAll();
    }

    public PedidoCompra buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
    }
}