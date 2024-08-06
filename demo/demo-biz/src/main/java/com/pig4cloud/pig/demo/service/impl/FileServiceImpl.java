package com.pig4cloud.pig.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.demo.entity.FileEntity;
import com.pig4cloud.pig.demo.mapper.FileMapper;
import com.pig4cloud.pig.demo.service.FileService;
import org.springframework.stereotype.Service;

/**
 * file 表
 *
 * @author pig
 * @date 2024-07-31 16:29:45
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

}