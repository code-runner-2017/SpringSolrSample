package com.pietro.solr.repository;

import java.util.List;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.pietro.solr.model.Product;

public interface ProductRepository extends SolrCrudRepository<Product, String> {

    List<Product> findByNameStartingWith(String name);
}