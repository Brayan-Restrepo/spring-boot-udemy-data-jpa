package com.udemy.app.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.udemy.app.models.entity.Cliente;
import com.udemy.app.models.service.IClienteService;
import com.udemy.app.models.service.IUploadFileService;
import com.udemy.app.util.paginator.PageRender;

/**
 * El @SessionAttributes("Cliente") se utiliza para editar guarda id en la session y no en
 * un input oculto es mas seguro
 * @author Brayan Restrepo
 *
 */
@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	/**
	 * @Qualifier("clienteDaoJPA") 
	 * Para saber que conexion utilizar, por si hay varias conexiones
	 */
	@Autowired
	private IClienteService iClienteService;
	@Autowired
	private IUploadFileService iUploadFileService;
	
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Cliente cliente = iClienteService.findOne(id);
		
		if(cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente "+cliente.getNombre());
		return "ver";
	}
	
	@RequestMapping(value="/listar", method=RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		Pageable pageRequest = new PageRequest(page, 5);
		Page<Cliente> clientes = this.iClienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
		
		model.addAttribute("titulo", "Listar clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}
	
	/**
	 * Crea la vista del formulario y le envia los campos de la entidad
	 * no crea ni guarda solo muestra el formulario de creacion
	 */
	@RequestMapping(value="/form")
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario Clente");		
		return "form";
	}
	/**
	 * Otra forma de mostrar recursos al cliente
	 * Expresion regular que permite que spring no borre la extencion de la foto
	 * filename:.+
	 */
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso = iUploadFileService.ver(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachmen; filename=\""+recurso.getFilename()+"\"")
				.body(recurso);
	}
	
	
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario Clente");
			return "form";
		}
		
		//Guardar la foto en una ruta
		if(!foto.isEmpty()) {
			
			if(cliente.getId() != null 
					&& cliente.getId() > 0 
					&& cliente.getFoto() != null ) {
				this.iUploadFileService.delete(cliente.getFoto());
			}
			this.iUploadFileService.guardar(foto);			
			cliente.setFoto(foto.getOriginalFilename());
		}
		
		this.iClienteService.save(cliente);
		//Se elimina la session
		status.setComplete();
		flash.addFlashAttribute("success", "Cliente creado con exito");
		return "redirect:/listar";
	}
	
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = null;
		if(id>0) {
			cliente = this.iClienteService.findOne(id);
			if(cliente == null) {
				flash.addFlashAttribute("error", "El cliente no existe");
				return "redirect:/listar";		
			}
			model.put("cliente", cliente);
			model.put("titulo", "Editar Cliente");
			return "form";
		}else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser 0");
			return "redirect:/listar";	
		}
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String delete(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		if(id>0) {
			Cliente cliente = this.iClienteService.findOne(id);
			
			this.iClienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado");
						
			if(this.iUploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto "+cliente.getFoto()+" Eliminada");					
			}
		}
		return "redirect:/listar";
	}
}
