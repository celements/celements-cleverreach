package com.celements.cleverreach;

import javax.validation.constraints.NotNull;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface CleverReachService {

  /**
   * Initialise the CleverReach Service
   *
   * @param clientId
   *          Client application ID
   * @param clientSecret
   *          Client application shared secret
   */
  boolean init(@NotNull String clientId, @NotNull String clientSecret);

  /**
   * Initialise the CleverReach Service
   *
   * @param restBaseUrl
   *          Base URL used for all requests
   * @param clientId
   *          Client application ID
   * @param clientSecret
   *          Client application shared secret
   */
  boolean init(@NotNull String restBaseUrl, @NotNull String clientId, @NotNull String clientSecret);

  /**
   * Initialise loading the config from XWikiPreferences
   */
  boolean initFromConfig();

  /**
   * Update the Tagesagenda mailing content
   */
  boolean updateMailing(String mailingId, String subject, String contentHtml,
      String contentPlain);

  /**
   * For debugging only. Returns the logged in user.
   *
   * @return The application creator info as JSON
   */
  @NotNull
  String whoami();

  /**
   * Get the TTL for the token.
   *
   * @return The TTL and Expiration date as JSON
   */
  @NotNull
  String ttl();

}
