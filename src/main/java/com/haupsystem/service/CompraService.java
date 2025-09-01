package com.haupsystem.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.haupsystem.model.Compra;
import com.haupsystem.model.Compra.EtapaCompra;
import com.haupsystem.model.CompraDto;
import com.haupsystem.model.CompraDtoMapper;
import com.haupsystem.model.CompraItem;
import com.haupsystem.model.CompraItemDto;
import com.haupsystem.model.CompraItemOrcamento;
import com.haupsystem.model.CompraPageDto;
import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioCompra;
import com.haupsystem.repository.RepositorioCompraItem;
import com.haupsystem.repository.RepositorioCompraItemOrcamento;
import com.haupsystem.repository.RepositorioUsuario;
import com.haupsystem.security.UserSpringSecurity;
import com.haupsystem.service.exception.AuthorizationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraService {

	@Autowired
    private final RepositorioCompra repositorioCompra;
	@Autowired
	private final RepositorioUsuario repositorioUsuario;
	@Autowired
	private final RepositorioCompraItem repositorioCompraItem;
	@Autowired
	private final RepositorioCompraItemOrcamento repositorioCompraItemOrcamento;

    // Lista apenas as compras do usuário logado
    public List<Compra> listarComprasUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repositorioUsuario.findByUsername(username);
        if(usuario == null) {
        	new RuntimeException("Usuário não encontrado");
        }
        return repositorioCompra.findBySolicitante(usuario);
    }

    public Long criarCompra(CompraDto compra) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repositorioUsuario.findByUsername(username);
        if(usuario == null) {
        	new RuntimeException("Usuário não encontrado");
        }
        
        validaDtoInclusaoCompra(compra);
        
        Compra novaCompra = new Compra();
        novaCompra.setSolicitante(usuario);
        novaCompra.setAtiva(true);
        novaCompra.setDataHoraInclusao(new Date());
        
        novaCompra.setDescricao(compra.getDescricao());
        novaCompra.setEtapa(compra.getEtapa());
        
        novaCompra = repositorioCompra.save(novaCompra);
        
        for(CompraItemDto item : compra.getItens()) {
        	CompraItem novoItem = new CompraItem();
        	novoItem.setDataHoraInclusao(new Date());
        	novoItem.setCompra(novaCompra);
        	novoItem.setNome(item.getNome());
        	novoItem.setQuantidade(item.getQuantidade());
        	repositorioCompraItem.save(novoItem);
        }
        
        return novaCompra.getId();
    }
    
    private void validaDtoInclusaoCompra(CompraDto dto) {
    	if(dto.getDescricao() == null) {
    		new RuntimeException("Descrição não informada");
    	}
    }
    
    public CompraDto findById(Long id) {
        UserSpringSecurity usuarioSpringSecurity = authenticated();
        if (!Objects.nonNull(usuarioSpringSecurity)) throw new AuthorizationException("Acesso negado!");
        Optional<Compra> compra = this.repositorioCompra.findById(id);
        
        CompraDto dto = convertToDto(compra.get());
        List<CompraItemDto> listaDtoItem = new ArrayList<>();
        List<CompraItem> listaItens = repositorioCompraItem.findByCompra(compra.get());
        listaItens.forEach(item-> {
        	CompraItemDto itemDto = new CompraItemDto();
        	itemDto.setId(item.getId());
        	itemDto.setNome(item.getNome());
        	itemDto.setQuantidade(item.getQuantidade());
        	itemDto.setObservacoes(item.getObservacoes());
        	itemDto.setAprovado(item.getAprovado());
        	itemDto.setMotivoRecusa(item.getMotivoRecusa());
        	listaDtoItem.add(itemDto);
        });
        
        dto.setItens(listaDtoItem);
        
        return dto;
    }
    
    public static UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
    
    public CompraDto avancarEtapa(CompraDto compraDto) {
    	if(compraDto.getId() == null) {
    		compraDto.setId(criarCompra(compraDto));
    	}
        Compra entity = repositorioCompra.findById(compraDto.getId())
            .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

        List<String> listaErros = new ArrayList<>();
        validaEtapaAtual(listaErros, compraDto);

        if (!listaErros.isEmpty()) {
            throw new RuntimeException(listaErros.get(0));
        }

        switch (entity.getEtapa()) {
            case CRIACAO: {
            	salvaDadosEtapaCriacao(entity, compraDto);
            	entity.setEtapa(EtapaCompra.ORCAMENTO);
                break;
            }
            case ORCAMENTO: {
            	entity.setDataHoraInclusaoOrcamento(new Date());
                entity.setEtapa(EtapaCompra.AGUARDANDO_APROVACAO);
                break;
            }
            case AGUARDANDO_APROVACAO: {
                
                if (compraDto.getItens() == null || compraDto.getItens().isEmpty()) {
                    throw new RuntimeException("Nenhum item encontrado para aprovação.");
                }

                long aprovados = compraDto.getItens().stream().filter(i -> Boolean.TRUE.equals(i.getAprovado())).count();
                long recusados = compraDto.getItens().stream().filter(i -> Boolean.FALSE.equals(i.getAprovado())).count();

                if (aprovados > 0 && recusados == 0) {
                    entity.setEtapa(EtapaCompra.APROVADA);
                    entity.setDataHoraCompraAprovada(new Date());
                } else if (aprovados > 0 && recusados > 0) {
                    entity.setEtapa(EtapaCompra.APROVADA_PARCIAL);
                    entity.setDataHoraCompraAprovada(new Date());
                } else if (aprovados == 0 && recusados > 0) {
                    entity.setEtapa(EtapaCompra.RECUSADA);
                    entity.setDataHoraCompraRecusada(new Date());
                } else {
                    throw new RuntimeException("Itens sem decisão. Todos devem ser aprovados ou recusados.");
                }
                
                for(CompraItemDto itemDto : compraDto.getItens()) {
                	
                	CompraItem item = repositorioCompraItem.findById(itemDto.getId()).get();
                	
                	if(itemDto.getAprovado()) {
                		CompraItemOrcamento orcamentoSelecionado = repositorioCompraItemOrcamento.findById(itemDto.getIdOrcamentoSelecionado()).get();
                		item.setOrcamentoSelecionado(orcamentoSelecionado);
                	}else {
                		item.setMotivoRecusa(itemDto.getMotivoRecusa());
                	}
                	item.setAprovado(itemDto.getAprovado());
                	
                	repositorioCompraItem.save(item);
                	
                }
                
                break;
            }
            case APROVADA: 
            case APROVADA_PARCIAL: {
                entity.setEtapa(EtapaCompra.REALIZADA);
                break;
            }
            case REALIZADA: {
                entity.setEtapa(EtapaCompra.FINALIZADA);
                entity.setDataHoraFinalizada(new Date());
                break;
            }
            case RECUSADA:
            case FINALIZADA: {
                throw new RuntimeException("Não é possível avançar uma compra já encerrada.");
            }
            default:
                throw new IllegalArgumentException("Etapa inesperada: " + entity.getEtapa());
        }
        
        Compra saved = repositorioCompra.save(entity);
        return CompraDtoMapper.toDto(saved);
    }
    
    public CompraDto salvar(CompraDto compraDto) {
    	
    	if(compraDto.getId() == null) {
    		compraDto.setId(criarCompra(compraDto));
    	}
    	
        Compra entity = repositorioCompra.findById(compraDto.getId())
            .orElseThrow(() -> new RuntimeException("Compra não encontrada"));
        
        switch (entity.getEtapa()) {
            case CRIACAO: {
            	salvaDadosEtapaCriacao(entity, compraDto);
                break;
            }
            case ORCAMENTO: {
                break;
            }
            case AGUARDANDO_APROVACAO: {
                break;
            }
            case APROVADA: 
            case APROVADA_PARCIAL: {
                break;
            }
            case REALIZADA: {
                break;
            }
            case RECUSADA:
            case FINALIZADA: {
                throw new RuntimeException("Não é possível salvar uma compra já encerrada.");
            }
            default:
                throw new IllegalArgumentException("Etapa inesperada: " + entity.getEtapa());
        }
        
        Compra saved = repositorioCompra.save(entity);
        return CompraDtoMapper.toDto(saved);
    }
    
    private void deletaItens(Long idCompra, List<CompraItemDto> listaItensFront) {
    	List<CompraItem> itensBanco = repositorioCompraItem.findByCompraId(idCompra);

        if (itensBanco == null || itensBanco.isEmpty()) {
            return;
        }

        Set<Long> idsFront = listaItensFront.stream()
                .map(CompraItemDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CompraItem> itensExcluidos = itensBanco.stream()
                .filter(item -> !idsFront.contains(item.getId()))
                .collect(Collectors.toList());
        
        for(CompraItem item : itensExcluidos) {
        	repositorioCompraItem.delete(item);
        }
    }
    
    private void validaEtapaAtual(List<String> listaErros, CompraDto compra) {
    	switch (compra.getEtapa()) {
			case CRIACAO: {
				validaEtapaCriacao(listaErros, compra);
				break;
			}
			case ORCAMENTO: {
				validaEtapaOrcamento(listaErros, compra);
				break;
			}
			case AGUARDANDO_APROVACAO: {
				break;
			}
			case RECUSADA: {
				break;
			}
			case REALIZADA: {
				break;
			}
			case FINALIZADA: {
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + compra.getEtapa());
		}
    }
    
    private void validaEtapaCriacao(List<String> listaErros, CompraDto compra) {
    	if(compra.getDescricao() == null || compra.getDescricao().isEmpty()) {
    		listaErros.add("Informe uma descrição");
    		return;
    	}
    	
    	if(compra.getItens().size() <= 0) {
    		listaErros.add("Informe pelo menos 1 item");
    		return;
    	}
    	
    	compra.getItens().forEach(item->{
    		if(item.getNome() == null || item.getNome().isEmpty()) {
    			listaErros.add("Informe o nome do item");
    			return;
    		}
    		
    		if(item.getQuantidade() <= 0) {
    			listaErros.add("Informe uma quantidade válida para o item " + item.getNome());
    			return;
    		}
    	});
    }
    
    private void validaEtapaOrcamento(List<String> listaErros, CompraDto compra) {
    	compra.getItens().forEach(item -> {
    		List<CompraItemOrcamento> listaOrcamentos = repositorioCompraItemOrcamento.findByCompraItemId(item.getId());
    		
    		if(listaOrcamentos.isEmpty()) {
    			throw new RuntimeException("Não foi encontrado nenhum orcamento para o item (ID:"+ item.getId() + ") " + item.getNome());
    		}
    	});
    }
    
    private void salvaDadosEtapaCriacao(Compra entity, CompraDto compraDto) {
    	entity.setDescricao(compraDto.getDescricao());
    	
    	if(entity.getDataHoraInclusao() == null) {
    		entity.setDataHoraInclusaoOrcamento(new Date());
    	}
    	
    	deletaItens(compraDto.getId(), compraDto.getItens());
    	
        for(CompraItemDto itemDto : compraDto.getItens()) {
        	CompraItem item = new CompraItem();
        	
        	if(itemDto.getId() != null) {
        		item = repositorioCompraItem.findById(itemDto.getId()).get();
        	}else {
        		item.setDataHoraInclusao(new Date());
        		item.setCompra(entity);
        	}
        	item.setNome(itemDto.getNome());
    		item.setQuantidade(itemDto.getQuantidade());
        	repositorioCompraItem.save(item);
        }
    }
    
    public CompraPageDto listarComprasPaginadas(int page, int size, String search, String sortBy, String sortDir) {
    	
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repositorioUsuario.findByUsername(username);
        if(usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "dataHoraInclusao");
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Specification<Compra> spec = Specification.where(null);
        
        spec = spec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("solicitante"), usuario));
        
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("descricao")), 
                        "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("etapa").as(String.class)), 
                        "%" + search.toLowerCase() + "%")
                ));
        }
        
        Page<Compra> pageResult = repositorioCompra.findAll(spec, pageable);
        
        List<CompraDto> comprasDto = pageResult.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        CompraPageDto response = new CompraPageDto();
        response.setContent(comprasDto);
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setFirst(pageResult.isFirst());
        response.setLast(pageResult.isLast());
        response.setHasNext(pageResult.hasNext());
        response.setHasPrevious(pageResult.hasPrevious());
        
        return response;
    }
    
    private CompraDto convertToDto(Compra compra) {
        CompraDto dto = new CompraDto();
        dto.setId(compra.getId());
        dto.setDescricao(compra.getDescricao());
        dto.setEtapa(compra.getEtapa());
        dto.setDataHoraInclusao(compra.getDataHoraInclusao());
        dto.setDataHoraInclusaoOrcamento(compra.getDataHoraInclusaoOrcamento());
        dto.setDataHoraCompraAprovada(compra.getDataHoraCompraAprovada());
        dto.setDataHoraCompraRecusada(compra.getDataHoraCompraRecusada());
        dto.setDataHoraFinalizada(compra.getDataHoraFinalizada());
        dto.setAtiva(compra.getAtiva());
        dto.setSolicitanteId(compra.getSolicitante().getId());
        dto.setSolicitanteNome(compra.getSolicitante().getNome());
        return dto;
    }
}
