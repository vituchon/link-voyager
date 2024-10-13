var LinkVoyager;
(function (LinkVoyager) {

  function levelize(links, root) {
    computeRootLevel(links,root)
    let hasChanged = computeFollowingLevel(links)
    while (hasChanged) {
      hasChanged = computeFollowingLevel(links)
    }
  }

  function computeRootLevel(links, root) {
    for (let index = 0; index < links.length; index++) {
      const link = links[index];
      if (link.source === root) {
        link.level = 0
      }
    }
  }

  function computeFollowingLevel(links) {
    var hasChanged = false
    const results = split(links, (link) => link.level !== undefined)
    const leveled = results.passed
    const unleveled = results.rejected
    for (let index = 0; index < unleveled.length; index++) {
      const unleveledLink = unleveled[index];
      for (let index = 0; index < leveled.length; index++) {
        const leveledLink = leveled[index];
        if (leveledLink.target === unleveledLink.source) {
          unleveledLink.level = leveledLink.level + 1
          hasChanged = true
        }
      }
    }
    return hasChanged
  }

  /** Divides in two an array using a predicate as separator rule. */
  function split(array, predicate) {
    const results = array.reduce(([passed, rejected], elem) => {
      if (predicate(elem)) {
        passed.push(elem);
      } else {
        rejected.push(elem)
      }
      return [passed, rejected]
    }, [[], []]);
    return {
      passed: results[0],
      rejected: results[1]
    }
  }


  const defaultServerRootUrl = '/link-voyager-maven'
  class Api {
    constructor(serverRootUrl) {
      this.serverRootUrl = serverRootUrl;
    }

    async startExploration(rootUrl, deep, workers) {
      try {
        const response = await fetch(`${this.serverRootUrl}/explorer?rootURL=${encodeURIComponent(rootUrl)}&deep=${deep}&workers=${workers}`);
        const textResponse = await response.text();
        const uuidMatch = textResponse.match(/([a-f0-9-]{36})/);
        if (uuidMatch) {
          return uuidMatch[0];
        } else {
          throw new Error("No se pudo extraer UUID de la respuesta.");
        }
      } catch (error) {
        console.error("Error en startExploration:", error);
        throw error;
      }
    }

    async checkStatus(uuid) {
      try {
        const response = await fetch(`${this.serverRootUrl}/explorer?uuid=${uuid}`);
        return await response.text();
      } catch (error) {
        console.error("Error en checkStatus:", error);
        throw error;
      }
    }

    async getLinks(uuid, rootUrl) {
      try {
        const response = await fetch(`${this.serverRootUrl}/explorer?uuidJson=${uuid}`);
        const links = await response.json();
        levelize(links, rootUrl)
        return links
      } catch (error) {
        console.error("Error en fetchJson:", error);
        throw error;
      }
    }
  }

  LinkVoyager.DefaultApi = new Api(defaultServerRootUrl);
})(LinkVoyager || (LinkVoyager = {}));


