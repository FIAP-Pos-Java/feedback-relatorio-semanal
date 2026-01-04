package com.feedback.repository;

import com.feedback.model.Feedback;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackRepository implements PanacheRepositoryBase<Feedback, String> {

    private static final Logger LOG = Logger.getLogger(FeedbackRepository.class);

    @Transactional
    public Feedback salvar(Feedback feedback) {
        try {
            persist(feedback);
            return feedback;
        } 
        catch (Exception e) 
        {
            LOG.errorf(e, "Erro ao salvar feedback. ID: %s", feedback.getId());
            throw new RuntimeException("Erro ao salvar feedback", e);
        }
    }

    public List<Feedback> buscarPorPeriodo(int dias) {
        try {
            var dataLimite = LocalDateTime.now().minusDays(dias);
            return find("dataCriacao >= ?1", dataLimite).list();
        } 
        catch (Exception e) 
        {
            LOG.errorf(e, "Erro ao buscar feedbacks. Dias: %d", dias);
            throw new RuntimeException("Erro ao buscar feedbacks", e);
        }
    }

    public Feedback buscarPorId(String id) {
        try {
            return findById(id);
        } 
        catch (Exception e) 
        {
            LOG.errorf(e, "Erro ao buscar feedback. ID: %s", id);
            throw new RuntimeException("Erro ao buscar feedback", e);
        }
    }

    public List<Feedback> buscarTodos() {
        return listAll();
    }

    public long contarCriticos() {
        return count("critico = true");
    }
}

