package com.haupsystem.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.haupsystem.model.Arquivo;
import com.haupsystem.model.ArquivoDto;
import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioArquivo;
import com.haupsystem.repository.RepositorioUsuario;


@Service
public class ArquivoService  {
	
	@Autowired
	RepositorioArquivo repositorioArquivo;
	
	@Autowired
	RepositorioUsuario repositorioUsuario;
	
	@Autowired
	ArquivoServiceHelper arquivoServiceHelper;
	
	@Autowired
	SupabaseStorageService supabaseStorageService;

	public Arquivo incluir(MultipartFile file) {

		try {
			
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
	        Usuario usuarioLogado = repositorioUsuario.findByUsername(username);
	        
	        String originalFilename = file.getOriginalFilename();
	        String extensao = "";

	        if (originalFilename != null && originalFilename.contains(".")) {
	            extensao = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	        }

			File f = new File(arquivoServiceHelper.montarDiretorioArquivo(extensao));

			Files.createDirectories(f.getParentFile().toPath());
			
			Files.write(f.toPath(), file.getBytes());

			return arquivoServiceHelper.registrarArquivoJaNoDisco(f.getAbsolutePath(), file.getOriginalFilename(), usuarioLogado);

		} catch (IOException e) {
			throw new RuntimeException("Erro ao salvar o arquivo", e);
		}

	}
		
	public void deletar(Arquivo arquivo) {
		try {

			Path filePath = Paths.get(arquivo.getDiretorio());
			Files.deleteIfExists(filePath);
			repositorioArquivo.delete(arquivo);

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public ArquivoDto convertToDto(Arquivo arquivo) {
		ArquivoDto dto = new ArquivoDto();
        dto.setId(arquivo.getId());
        dto.setDataHoraInclusao(arquivo.getDataHoraInclusao());
        dto.setNomeUsuarioInclusao(arquivo.getUsuarioInclusao().getNome());
        dto.setIdUsuarioInclusao(arquivo.getUsuarioInclusao().getId());
        dto.setDiretorio(arquivo.getDiretorio());
		dto.setTamanho(arquivo.getTamanho());
		dto.setNomeOriginal(arquivo.getNomeOriginal());
		dto.setExtensao(arquivo.getExtensao());
        return dto;
    }
	
	public MediaType obterMediaTypeArquivo(Path caminhoArquivo) {
		String contentType = null;
		try {
			contentType = Files.probeContentType(caminhoArquivo);
		} catch (IOException e) { 
			//NADA A FAZER
		}		
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return MediaType.parseMediaType(contentType);
	}
	
	public Arquivo incluirNoSupabase(MultipartFile file) {
		try {
			
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
	        Usuario usuarioLogado = repositorioUsuario.findByUsername(username);
	        
	        String diretorioSupabase = supabaseStorageService.uploadFile(file.getOriginalFilename(), file.getBytes());
	        
	        Arquivo arquivo = new Arquivo();
			arquivo.setDataHoraInclusao(new Date());
			arquivo.setUsuarioInclusao(usuarioLogado);
			arquivo.setDiretorio(diretorioSupabase);
			File filed = new File(diretorioSupabase);
			arquivo.setTamanho(filed.length());
			arquivo.setNomeOriginal(file.getOriginalFilename());
			
			String extensao = "";
			if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
			    extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			}
			arquivo.setExtensao(extensao);
			
			repositorioArquivo.save(arquivo);

			return arquivo;

		} catch (IOException e) {
			throw new RuntimeException("Erro ao salvar o arquivo", e);
		}
	}
	

}
