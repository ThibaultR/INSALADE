<?php

namespace Insalade\CommunicationBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Insalade\CommunicationBundle\Entity\Post;
use Insalade\CommunicationBundle\Entity\Push;
use Insalade\UserBundle\Entity\Asso;
use Insalade\UserBundle\Entity\Amicale;

class CommunicationListener
{
    private $tokenStorage;

    public function __construct(\Swift_Mailer $mailer, TokenStorageInterface $tokenStorage)
    {
        $this->mailer = $mailer;
        $this->tokenStorage = $tokenStorage;
    }

    public function postPersist(LifecycleEventArgs $args)
    {
        $entity = $args->getEntity();
        $entityManager = $args->getEntityManager();
        $user = $this->tokenStorage->getToken()->getUser();

        if ($entity instanceof Post) {
            $message = \Swift_Message::newInstance()
                ->setSubject('Un nouveau post à valider !')
                ->setFrom('insalade@gmail.com')
                ->setTo('insalade@gmail.com')
                ->setBody("http://37.59.123.110/Web/web/post/".$entity->getId())
            ;
            $this->mailer->send($message);
        }

        if ($entity instanceof Push) {
            $message = \Swift_Message::newInstance()
                ->setSubject('Un nouveau push à valider !')
                ->setFrom('insalade@gmail.com')
                ->setTo('insalade@gmail.com')
                ->setBody("http://37.59.123.110/Web/web/push/".$entity->getId())
            ;
            $this->mailer->send($message);
        }

        if ($user instanceof Asso) {
            $entity->setParentId($user->getParentId());
        }
        if ($user instanceof Amicale) {
            $entity->setParentId($user->getId());
        }
    }

    public function postUpdate(LifecycleEventArgs $args)
    {
        $entity = $args->getEntity();
        $entityManager = $args->getEntityManager();

        if ($entity instanceof Post) {
            $message = \Swift_Message::newInstance()
                ->setSubject('Un nouveau post à valider !')
                ->setFrom('insalade@gmail.com')
                ->setTo('insalade@gmail.com')
                ->setBody("http://37.59.123.110/Web/web/post/".$entity->getId())
            ;
            $this->mailer->send($message);
        }

        if ($entity instanceof Push) {
            $message = \Swift_Message::newInstance()
                ->setSubject('Un nouveau push à valider !')
                ->setFrom('insalade@gmail.com')
                ->setTo('insalade@gmail.com')
                ->setBody("http://37.59.123.110/Web/web/push/".$entity->getId())
            ;
            $this->mailer->send($message);
        }
    }
}