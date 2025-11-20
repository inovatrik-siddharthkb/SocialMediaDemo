package com.social.media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.social.media.service.FileStorageService;

@RestController
@RequestMapping("/files")
public class FileController 
{
	@Autowired
	private FileStorageService fileService;
	
	@GetMapping("/{filename}")
	public ResponseEntity<byte[]> getFile(@PathVariable String filename)
	{
		byte[] data = fileService.loadFile(filename);
		
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
	}
}
