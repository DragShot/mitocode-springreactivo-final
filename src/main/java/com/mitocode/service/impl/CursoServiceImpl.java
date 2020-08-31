package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitocode.document.Curso;
import com.mitocode.repo.ICursoRepo;
import com.mitocode.repo.IRepo;
import com.mitocode.service.ICursoService;

@Service
public class CursoServiceImpl extends CRUDImpl<Curso, String> implements ICursoService {
	@Autowired
	private ICursoRepo repo;
	
	@Override
	protected IRepo<Curso, String> getRepo() {
		return repo;
	}
}
