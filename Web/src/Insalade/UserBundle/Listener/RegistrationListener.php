<?php
namespace Insalade\UserBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Insalade\UserBundle\Entity\Asso;
use Symfony\Component\DependencyInjection\ContainerInterface;

class RegistrationListener
{
    private $container;
    private $mailer;

    public function __construct(ContainerInterface $container, \Swift_Mailer $mailer) {
        $this->mailer = $mailer;
        $this->container = $container;
    }

    public function postPersist(LifecycleEventArgs $args)
    {
        $entity = $args->getEntity();
        $entityManager = $args->getEntityManager();

        // peut-être voulez-vous seulement agir sur une entité « Product »
        if ($entity instanceof User) {
            $entity->setEnabled(false);
        }

        if ($entity instanceof Asso) {
            $entity->setEnabled(false);
            $entity->setParentId(2);

            $message = \Swift_Message::newInstance()
                ->setSubject('Un nouveau compte Asso a été crée !')
                ->setFrom('insalade@gmail.com')
                ->setTo('insalade@gmail.com')
                ->setBody("".$entity->getUsername())
            ;
            $this->mailer->send($message);
        }

        if ($entity instanceof Amicale) {
            $entity->setEnabled(true);
        }

        if ($entity instanceof Insalade) {
            $entity->setEnabled(true);
        }

        $entityManager->flush();
    }
}

