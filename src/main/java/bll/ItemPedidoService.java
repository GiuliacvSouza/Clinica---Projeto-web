package bll;

import dal.ItemPedidoRepository;
import model.ItemPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository repository;

    public ItemPedidoService(ItemPedidoRepository repository) {
        this.repository = repository;
    }

    public ItemPedido adicionarItem(ItemPedido item) {

        if (item.getIdMaterial() == null) {
            throw new RuntimeException("Item precisa de material.");
        }

        if (item.getIdPedido() == null) {
            throw new RuntimeException("Item precisa de pedido.");
        }

        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade inválida.");
        }

        if (item.getValor() == null || item.getValor().doubleValue() <= 0) {
            throw new RuntimeException("Valor inválido.");
        }

        return repository.save(item);
    }

    public List<ItemPedido> listarTodos() {
        return repository.findAll();
    }

}