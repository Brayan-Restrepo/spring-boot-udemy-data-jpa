package com.udemy.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	@Override
	public boolean delete(String nombre) {
		Path path = Paths.get("uploads").resolve(nombre).toAbsolutePath();
		File file = path.toFile();		
		if(file.exists()) {
			if(file.delete()) {		
				return true;			
			}
		}
		return false;
	}

	@Override
	public void guardar(MultipartFile foto) {

		//Guardar la imagen en una carpeta dentor del proyecto
		//Path directorioRecursos =Paths.get("src//main/resources/static/uploads");			
		//String rootPath = directorioRecursos.toFile().getAbsolutePath();
		String rootPath = Paths.get("uploads").toAbsolutePath().toString();
		try {
			//obtenemos los bytes del la img
			byte[] bytes = foto.getBytes();
			//Crea la uta completa con el nombre del file
			Path rutaCompleta = Paths.get(rootPath+"/"+foto.getOriginalFilename());
			//Escribir el directorio en la ruta
			Files.write(rutaCompleta, bytes);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Resource ver(String ruta) {
		Resource recurso = null;		 
		try {
			Path pathFoto = Paths.get("uploads").resolve(ruta).toAbsolutePath();
			recurso = new UrlResource(pathFoto.toUri());
			// Si no existe o si no es leeible sale una exception
			if(!recurso.exists()) {
				throw new RuntimeException("Error no se puede cargar la imagen: "+pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recurso;
	}
}
