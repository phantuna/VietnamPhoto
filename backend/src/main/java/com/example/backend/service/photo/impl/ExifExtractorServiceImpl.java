package com.example.backend.service.photo.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import com.example.backend.dto.response.photo.ExifDataDto;
import com.example.backend.service.photo.ExifExtractorService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExifExtractorServiceImpl implements ExifExtractorService {

    @Override
    public ExifDataDto extract(MultipartFile file) {
        ExifDataDto dto = new ExifDataDto();

        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (ifd0 != null) {
                dto.setCameraMake(ifd0.getString(ExifIFD0Directory.TAG_MAKE));
                dto.setCameraModel(ifd0.getString(ExifIFD0Directory.TAG_MODEL));
            }

            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (subIFD != null) {
                dto.setIso(subIFD.getInteger(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));

                Double aperture = subIFD.getDoubleObject(ExifSubIFDDirectory.TAG_FNUMBER);
                if (aperture != null) {
                    dto.setAperture(BigDecimal.valueOf(aperture).setScale(2, RoundingMode.HALF_UP));
                }

                Double focalLength = subIFD.getDoubleObject(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
                if (focalLength != null) {
                    dto.setFocalLength(BigDecimal.valueOf(focalLength).setScale(2, RoundingMode.HALF_UP));
                }

                dto.setLensModel(subIFD.getString(ExifSubIFDDirectory.TAG_LENS_MODEL));
                dto.setShutterSpeed(subIFD.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            }

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    dto.setGpsLatitude(BigDecimal.valueOf(geoLocation.getLatitude()).setScale(7, RoundingMode.HALF_UP));
                    dto.setGpsLongitude(BigDecimal.valueOf(geoLocation.getLongitude()).setScale(7, RoundingMode.HALF_UP));
                }
            }

            return dto;
        } catch (Exception e) {
            return dto;
        }
    }
}