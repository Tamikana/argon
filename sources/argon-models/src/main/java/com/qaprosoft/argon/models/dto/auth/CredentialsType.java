package com.qaprosoft.argon.models.dto.auth;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class CredentialsType implements Serializable
{
	private static final long serialVersionUID = 1567014101763491651L;

	@NotNull
	private String username;

	@NotNull
	private String password;

	public CredentialsType()
	{
	}

	public CredentialsType(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
