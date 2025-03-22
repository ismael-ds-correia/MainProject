package com.qmasters.fila_flex.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.qmasters.fila_flex.dto.AdressDTO;
import com.qmasters.fila_flex.model.Adress;
import com.qmasters.fila_flex.repository.AdressRepository;

import jakarta.transaction.Transactional;

@Service
public class AdressService {

	private final AdressRepository adressRepository;

	public AdressService(AdressRepository adressRepository) {
		this.adressRepository = adressRepository;
	}
	
	@Transactional
	public Adress saveAdress(AdressDTO adressDTO) {
		Adress adress = new Adress(
			adressDTO.getNumber(), 
			adressDTO.getStreet(), 
			adressDTO.getCity(), 
			adressDTO.getState(), 
			adressDTO.getCountry()
		);
		
		return adressRepository.save(adress);
	}

	public List<Adress> getAllAdress() {
		return adressRepository.findAll();
	}

	public Optional<Adress> findAdressById(Long id) {
		return adressRepository.findById(id);
	}

	@Transactional
	public void deleteAdress(Long id) {
		if (adressRepository.existsById(id)) {
			adressRepository.deleteById(id);
		} else {
			throw new NoSuchElementException("Endereço não encontrado, remoção não foi realizada");
		}

	}

}
