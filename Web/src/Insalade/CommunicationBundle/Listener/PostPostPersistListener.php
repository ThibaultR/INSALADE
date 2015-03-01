<?php

namespace Insalade\CommunicationBundle\Listener;

use Doctrine\ORM\Event\LifecycleEventArgs;
use Insalade\CommunicationBundle\Entity\Post;

class PostPostPersistListener
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
                ->setTo('hkwon@insa-rennes.fr')
                ->setBody("http://37.59.123.110/Web/web/post/".$entity->getId())
            ;
            $this->mailer->send($message);
        }
    }
}