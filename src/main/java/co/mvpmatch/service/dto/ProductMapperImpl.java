package co.mvpmatch.service.dto;

import co.mvpmatch.domain.Product;
import co.mvpmatch.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO productToProductDTO(Product product) {
        ProductDTO productDTO = null;
        if (product != null) {
            productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setProductName(product.getName());
            productDTO.setAmountAvailable(product.getAmountAvailable());
            productDTO.setCost(product.getCost());
            productDTO.setSellerId(product.getSeller().getId());
        }
        return productDTO;
    }

    @Override
    public Product productDTOToProduct(ProductDTO productDTO) {

        Product product = null;
        if (productDTO != null) {
            product = new Product();
            product.setId(productDTO.getId());
            product.setName(productDTO.getProductName());
            product.setAmountAvailable(productDTO.getAmountAvailable());
            product.setCost(productDTO.getCost());
            User seller = new User();
            seller.setId(productDTO.getSellerId());
            product.setSeller(seller);
        }
        return product;
    }

    @Override
    public List<ProductDTO> productsToProductDTOs(List<Product> products) {
        return products.stream().filter(Objects::nonNull).map(this::productToProductDTO).collect(Collectors.toList());
    }

    @Override
    public List<Product> productsDTOToProducts(List<ProductDTO> productDTOs) {
        return productDTOs.stream().filter(Objects::nonNull).map(this::productDTOToProduct).collect(Collectors.toList());
    }
}
