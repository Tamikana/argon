package com.qaprosoft.argon.dbaccess.dao.mysql;

import com.qaprosoft.argon.models.db.Authority;
import com.qaprosoft.argon.models.db.Authority.AuthorityType;

public interface AuthorityDAO
{

	void createAuthority(Authority authority);

	void updateAuthority(Authority authority);

	Authority getAuthorityById(Long id);

	Authority getAuthorityByAuthority(AuthorityType authority);

	void deleteAuthorityByAuthority(AuthorityType authority);

	void deleteAuthorityById(Long id);

}