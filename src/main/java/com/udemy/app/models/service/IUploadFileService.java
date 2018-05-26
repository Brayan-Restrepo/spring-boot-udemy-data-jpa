package com.udemy.app.models.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	
	public boolean delete(String nombre);
	
	public void guardar(MultipartFile foto);

	public Resource ver(String ruta);
}

