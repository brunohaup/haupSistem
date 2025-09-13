package com.haupsystem.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.haupsystem.model.Arquivo;
import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioArquivo;

@Service
public class ArquivoServiceHelper {
	
	//{diretorio.arquivos}
	@Value("${supabase.url}")
    private String diretorioBase;
		
	@Autowired
	RepositorioArquivo repositorioArquivo;
	
	public String montarDiretorioArquivo(String extensao) {

        // Gerar ID único e extensão
        String idAcesso = UUID.randomUUID().toString();

        // Data atual para criar a estrutura de diretórios
        LocalDate hoje = LocalDate.now();
        String ano = String.valueOf(hoje.getYear());
        String mes = String.format("%02d", hoje.getMonthValue());

        // Criar diretório baseado na estrutura ano/mês
        Path uploadDir = Paths.get(diretorioBase, ano, mes);       

        // Nome do arquivo no disco
        String nomeArquivoSalvo = idAcesso + "." + extensao;
        Path filePath = uploadDir.resolve(nomeArquivoSalvo);
		return filePath.toString();

	}
	
	public Arquivo registrarArquivoJaNoDisco(String diretorioNomeArquivo, String nomeOriginalDoArquivo, Usuario usuarioLogado) throws IOException {
		
		File file = new File(diretorioNomeArquivo);
		
		Arquivo arquivo = new Arquivo();
		arquivo.setDataHoraInclusao(new Date());
		arquivo.setUsuarioInclusao(usuarioLogado);
		arquivo.setDiretorio(diretorioNomeArquivo);
		arquivo.setTamanho(file.length());
		arquivo.setNomeOriginal(nomeOriginalDoArquivo);
		
		String extensao = "";
		if (nomeOriginalDoArquivo != null && nomeOriginalDoArquivo.contains(".")) {
		    extensao = nomeOriginalDoArquivo.substring(nomeOriginalDoArquivo.lastIndexOf(".") + 1);
		}
		arquivo.setExtensao(extensao);
		
		repositorioArquivo.save(arquivo);
				
		return arquivo;
		
	}
	
}
