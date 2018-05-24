package com.udemy.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.udemy.app.models.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>{

	
}
