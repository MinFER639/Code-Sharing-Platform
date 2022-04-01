package platform;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, String> {
    CodeSnippet findCodeSnippetById(String id);

    List<CodeSnippet> findTop10ByIsSecretOrderByDateTimeDesc(boolean getIsSecret);
}
