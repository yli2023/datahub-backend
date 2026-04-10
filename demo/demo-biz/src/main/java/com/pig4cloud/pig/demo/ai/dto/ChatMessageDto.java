package com.pig4cloud.pig.demo.ai.dto;

import lombok.Data;

@Data
public class ChatMessageDto {

	/**
	 * user / assistant / system
	 */
	private String role;

	private String content;

}
