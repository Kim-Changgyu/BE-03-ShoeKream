package com.prgrms.kream.domain.product.service;

import static com.prgrms.kream.common.mapper.ProductMapper.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.kream.domain.product.dto.request.ProductGetAllRequest;
import com.prgrms.kream.domain.product.dto.request.ProductRegisterFacadeRequest;
import com.prgrms.kream.domain.product.dto.request.ProductUpdateFacadeRequest;
import com.prgrms.kream.domain.product.dto.response.ProductGetAllResponses;
import com.prgrms.kream.domain.product.dto.response.ProductGetFacadeResponse;
import com.prgrms.kream.domain.product.dto.response.ProductRegisterResponse;
import com.prgrms.kream.domain.product.dto.response.ProductUpdateResponse;
import com.prgrms.kream.domain.product.model.Product;
import com.prgrms.kream.domain.product.repository.ProductOptionRepository;
import com.prgrms.kream.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;

	@Transactional
	public ProductRegisterResponse register(ProductRegisterFacadeRequest productRegisterFacadeRequest) {
		Product product = productRepository.save(toProduct(productRegisterFacadeRequest));

		productOptionRepository.saveAll(
				toProductOptions(productRegisterFacadeRequest.sizes(), product)
		);

		return toProductRegisterResponse(product.getId());
	}

	@Transactional(readOnly = true)
	public ProductGetFacadeResponse get(Long id) {
		return toProductGetFacadeResponse(getProductEntity(id));
	}

	@Transactional(readOnly = true)
	public ProductGetAllResponses getAll(ProductGetAllRequest productGetAllRequest) {
		List<Product> products = productRepository.findAllByCursor(
				productGetAllRequest.cursorId(), productGetAllRequest.pageSize(), productGetAllRequest.searchWord());

		Long lastId = -1L;
		if (products.size() != 0) {
			lastId = products.get(products.size() - 1).getId();
		}

		return toProductGetAllResponses(products, lastId);
	}

	@Transactional
	public ProductUpdateResponse update(ProductUpdateFacadeRequest productFacadeUpdateRequest) {
		Product product = getProductEntity(productFacadeUpdateRequest.id());
		product.update(productFacadeUpdateRequest.releasePrice(), productFacadeUpdateRequest.description());

		productOptionRepository.deleteAllByProduct(product);
		productOptionRepository.saveAll(
				toProductOptions(productFacadeUpdateRequest.sizes(), product)
		);

		return toProductUpdateResponse(product.getId());
	}

	@Transactional
	public void delete(Long id) {
		Product product = getProductEntity(id);
		productOptionRepository.deleteAllByProduct(product);
		productRepository.delete(product);
	}

	private Product getProductEntity(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("productId does not exist"));
	}
}
