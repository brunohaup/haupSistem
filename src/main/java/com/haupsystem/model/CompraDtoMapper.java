package com.haupsystem.model;

public class CompraDtoMapper {

    public static CompraDto toDto(Compra entity) {
        if (entity == null) return null;

        CompraDto dto = new CompraDto();
        dto.setId(entity.getId());
        dto.setEtapa(entity.getEtapa());
        dto.setDescricao(entity.getDescricao());
        dto.setDataHoraInclusao(entity.getDataHoraInclusao());
        dto.setDataHoraInclusaoOrcamento(entity.getDataHoraInclusaoOrcamento());
        dto.setDataHoraCompraAprovada(entity.getDataHoraCompraAprovada());
        dto.setDataHoraCompraRecusada(entity.getDataHoraCompraRecusada());
        dto.setDataHoraFinalizada(entity.getDataHoraFinalizada());
        dto.setAtiva(entity.getAtiva());

        if (entity.getSolicitante() != null) {
            dto.setSolicitanteId(entity.getSolicitante().getId());
            dto.setSolicitanteNome(entity.getSolicitante().getNome()); 
            // supondo que Usuario tenha nome
        }

        return dto;
    }

    public static Compra toEntity(CompraDto dto) {
        if (dto == null) return null;

        Compra entity = new Compra();
        entity.setId(dto.getId());
        entity.setEtapa(dto.getEtapa());
        entity.setDescricao(dto.getDescricao());
        entity.setDataHoraInclusao(dto.getDataHoraInclusao());
        entity.setDataHoraInclusaoOrcamento(dto.getDataHoraInclusaoOrcamento());
        entity.setDataHoraCompraAprovada(dto.getDataHoraCompraAprovada());
        entity.setDataHoraCompraRecusada(dto.getDataHoraCompraRecusada());
        entity.setDataHoraFinalizada(dto.getDataHoraFinalizada());
        entity.setAtiva(dto.getAtiva());

        // Aqui você precisa buscar Usuario pelo id (ex.: via repository)
        // entity.setSolicitante(usuarioRepo.findById(dto.getSolicitanteId()).orElse(null));

        return entity;
    }
}

