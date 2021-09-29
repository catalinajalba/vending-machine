package co.mvpmatch.service.dto;

import co.mvpmatch.domain.Product;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductMapper {

    ProductDTO productToProductDTO(Product product);
    List<ProductDTO> productsToProductDTOs(List<Product> products);
    Product productDTOToProduct(ProductDTO productDTO);
    List<Product> productsDTOToProducts(List<ProductDTO> productDTOs);
}
