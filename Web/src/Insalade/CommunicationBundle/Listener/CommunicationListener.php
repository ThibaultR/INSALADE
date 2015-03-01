<?php

namespace Insalade\CommunicationBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Insalade\CommunicationBundle\Entity\Post;
use Insalade\CommunicationBundle\Entity\Push;

class CommunicationListener
{
    public function __construct(\Swift_Mailer $mailer)
    {
        $this->mailer = $mailer;
    }

    public function postPersist(LifecycleEventArgs $args)
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