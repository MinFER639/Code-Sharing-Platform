package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CodeSnippetService {

    @Autowired
    CodeSnippetRepository codeSnippetRepository;

    public Optional<CodeSnippet> getCodeSnippetById(String id) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            return Optional.of(codeSnippetRepository.findById(id).get());
        } else {
            return Optional.empty();
        }
    }

    public void saveCodeSnippet(CodeSnippet codeSnippet) {
        CodeSnippet savedCodeSnippet = codeSnippetRepository.save(codeSnippet);
    }

    public void deleteAllCodeSnippet() {
        codeSnippetRepository.deleteAll();
    }

    public List<CodeSnippet> getAllCodes() {
        return new ArrayList<>(codeSnippetRepository.findTop10ByIsSecretOrderByDateTimeDesc(false));
    }

    public void deleteCodeById(String id) {
        codeSnippetRepository.deleteById(id);
    }
}
