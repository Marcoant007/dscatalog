package com.marcoantonio.dscatalog.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3Service {

	private static Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Autowired
	private AmazonS3 s3client;

	@Value("${s3.bucket}")
	private String bucketName;

	public URL uploadFile(MultipartFile file) {
		try {
			String originalName = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalName);
			String fileName = Instant.now().toDate().getTime() + "." + extension;

			InputStream stream = file.getInputStream();
			String contentType = file.getContentType();
			return uploadFileAws(stream, fileName, contentType);

		} catch(IOException e){
			throw new IllegalArgumentException(e.getMessage());
		}
	
	}

	private URL uploadFileAws(InputStream stream, String fileName, String contentType) {
		ObjectMetadata metaData = new ObjectMetadata();
		LOG.info("Upload Start");
		s3client.putObject(bucketName, fileName, stream, metaData);
		LOG.info("Upload Finish");
		return s3client.getUrl(bucketName, fileName);
	}
}